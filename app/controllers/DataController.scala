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
import com.gu.scanamo.error.DynamoReadError
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


//      val queryParams = request.queryString.map {
//        case ("user", v) => Some(userIndex.query('userEmail -> v.mkString))
//        //      case("daterange",v)=>
//        //        val complete=v.mkString
//        //        val bounds=complete.split(",")
//        //        Between('dateCreated, Bounds(Bound(bounds.head),Bound(bounds(1))))
//        case _ => None
//      }.filter(_.isDefined)
      makeResult(Scanamo.exec(client)(table.query('userEmail->user)))
    }

  def all = Action { implicit request =>
    Logger.info(s"unattached atoms table is $tableName")

    val client = getClient

    val table = Table[UnattachedAtom](tableName.get)
    val dateIndex = table.index("dateIndex")

    val lastDayOfMonth = Seq(31, 29, 31, 30, 30, 31, 31, 30, 31, 31, 30, 31)

    request.queryString.get("month") match {
      case Some(monthStringSeq)=>
        val monthstring = monthStringSeq.mkString("")
        val ym = monthstring.split("_")
        val year=ym.head
        val month=ym(1).toInt

        val startString = f"$year-$month%02d-01T00:00:00Z"
        val lastDay = lastDayOfMonth(month-1)
        val endString = f"$year-$month%02d-${lastDay}T23:59:59.999Z"

        Logger.info(s"startString $startString")
        Logger.info(s"endString $endString")

        val bounds = Bounds(Bound(startString),Bound(endString))
        makeResult(Scanamo.exec(client)(dateIndex.query('dummy->"n" and ('dateCreated between bounds))))
      case None=>
        makeResult(Scanamo.exec(client)(table.scan()))
    }

  }
}
