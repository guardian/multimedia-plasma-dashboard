package controllers
import javax.inject._

import akka.actor.ActorSystem
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import com.gu.scanamo._
import com.gu.scanamo.syntax._
import models.{ErrorResponse, UnattachedAtom}
import play.api.Configuration
import akka.event.{DiagnosticLoggingAdapter, Logging}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{AWSCredentialsProviderChain, EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.gu.scanamo.error.{DynamoReadError, ScanamoError}
import com.gu.scanamo.ops.ScanamoOps
import com.gu.scanamo.query.{Between, Query}
import play.api.Logger
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DataController @Inject()(cc:ControllerComponents,config:Configuration,system:ActorSystem) extends AbstractController (cc){
  private val region = config.get[String]("region")

  protected def getClient:AmazonDynamoDB = {
    val chain = new AWSCredentialsProviderChain(
      new EnvironmentVariableCredentialsProvider(),
      new ProfileCredentialsProvider("multimedia"),
      new ProfileCredentialsProvider(),
      InstanceProfileCredentialsProvider.getInstance()
    )

    AmazonDynamoDBClientBuilder.standard()
      .withCredentials(chain)
      .withRegion(region)
      .build()
  }

  val tableName: Option[String] = config.get[Option[String]]("UnattachedAtomsTable")

  def boundsFromQueryArg(queryString:Map[String,Seq[String]]):Option[Bounds[String]] = {
    val lastDayOfMonth = Seq (31, 29, 31, 30, 30, 31, 31, 30, 31, 31, 30, 31)

    queryString.get("month").map(monthStringSeq=>{
      val monthstring = monthStringSeq.mkString ("")
      val ym = monthstring.split ("_")
      val year = ym.head
      val month = ym (1).toInt

      val startString = f"$year-$month%02d-01T00:00:00Z"
      val lastDay = lastDayOfMonth (month - 1)
      val endString = f"$year-$month%02d-${
        lastDay
      }T23:59:59.999Z"

      Logger.info (s"startString $startString")
      Logger.info (s"endString $endString")

      Bounds (Bound (startString), Bound (endString) )
    })
  }

  def makeResultNew[V](params:ScanamoOps[List[Either[DynamoReadError, V]]])(block: ScanamoOps[List[Either[DynamoReadError, V]]]=>List[Either[DynamoReadError, V]]) = {
    try {
      val result = block(params)
      val processed_result = result collectFirst { case x@Left(_) => x } getOrElse Right(result collect { case Right(x) => x })

      processed_result match {
        case Left(error) =>
          val response = ErrorResponse("error", error.toString)
          InternalServerError(response.asJson.toString)
        case Right(atomList: List[UnattachedAtom]) =>
          Ok(atomList.asJson.toString)
      }
    } catch {
      case excep:Any=>
        Logger.error(excep.toString)
        Logger.error(excep.getStackTrace.map(_.toString).mkString("\n"))
        val response = ErrorResponse("generic error", excep.toString, stackTrace = Some(excep.getStackTrace.map(_.toString)))
        InternalServerError(response.asJson.toString)
    }
  }

  def makeResult(result:List[Either[DynamoReadError, UnattachedAtom]]) = {
    //https://stackoverflow.com/questions/7230999/how-to-reduce-a-seqeithera-b-to-a-eithera-seqb
    val processed_result = result collectFirst { case x@Left(_) => x } getOrElse Right(result collect { case Right(x) => x })

    processed_result match {
      case Left(error) =>
        val response = ErrorResponse("error", error.toString)
        InternalServerError(response.asJson.toString)
      case Right(atomList: List[UnattachedAtom]) =>
        Ok(atomList.asJson.toString)
    }
  }

  def forUser(user:String) = Action { implicit request=>
    Logger.info(s"unattached atoms table is $tableName")

    val client = getClient

    val table = Table[UnattachedAtom](tableName.get)

    boundsFromQueryArg(request.queryString) match {
      case Some(bounds)=>
        makeResultNew[UnattachedAtom](table.query('userEmail->user and ('dateCreated between bounds))){ params=>
          Scanamo.exec(client)(params)
        }
      case None=>
        makeResultNew[UnattachedAtom](table.query('userEmail->user)) { params =>
          Scanamo.exec(client)(params)
        }
    }

  }

  def all = Action { implicit request =>
    Logger.info(s"unattached atoms table is $tableName")

    val client = getClient

    val table = Table[UnattachedAtom](tableName.get)
    val dateIndex = table.index("dateIndex")

    boundsFromQueryArg(request.queryString) match {
      case Some(bounds)=>
        makeResultNew[UnattachedAtom](dateIndex.query('dummy->"n" and ('dateCreated between bounds))) { params =>
          Scanamo.exec(client)(params)
        }
      case None=>
        makeResultNew[UnattachedAtom](null) { params =>
          Scanamo.exec(client)(table.scan())
        }
    }

  }
}
