package controllers
import javax.inject._

import akka.actor.ActorSystem
import play.api.mvc._

import com.gu.scanamo._
import com.gu.scanamo.syntax._
import models.{ErrorResponse,ConfigResponse, UnattachedAtom}
import play.api.Configuration
import akka.event.{DiagnosticLoggingAdapter, Logging}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{AWSCredentialsProviderChain, EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.gu.scanamo.error.{DynamoReadError, ScanamoError}
import com.gu.scanamo.ops.ScanamoOps
import play.api.Logger
import io.circe.generic.auto._
import io.circe.syntax._


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

  def formatFinalResult(result:List[UnattachedAtom],accept:String) = {
    if(accept=="application/json"){
      Ok(result.asJson.toString)
    } else if(accept=="text/plain"){
      Ok(result.map(_.AtomID).mkString("\n"))
    } else {
      BadRequest(s"format $accept is not supported")
    }
  }

  def makeResultNew[V](params:ScanamoOps[List[Either[DynamoReadError, V]]],accept:String="application/json")(block: ScanamoOps[List[Either[DynamoReadError, V]]]=>List[Either[DynamoReadError, V]]) = {
    try {
      val result = block(params)
      val processed_result = result collectFirst { case x@Left(_) => x } getOrElse Right(result collect { case Right(x) => x })

      processed_result match {
        case Left(error) =>
          val response = ErrorResponse("error", error.toString)
          InternalServerError(response.asJson.toString)
        case Right(atomList: List[UnattachedAtom]) =>
          formatFinalResult(atomList, accept)
      }
    } catch {
      case excep:Any=>
        Logger.error(excep.toString)
        Logger.error(excep.getStackTrace.map(_.toString).mkString("\n"))
        val response = ErrorResponse("generic error", excep.toString, stackTrace = Some(excep.getStackTrace.map(_.toString)))
        InternalServerError(response.asJson.toString)
    }
  }

  def removeParams(acceptFormat:String) = {
    val splitout = acceptFormat.split(";")
    splitout.head
  }

  def findPreferredFormat(acceptString:String):String = {
    val possibleAccept = acceptString.split("\\s*,\\s*").map(removeParams(_))

    if(possibleAccept.contains("application/json")) return "application/json"
    if(possibleAccept.contains("text/plain")) return "text/plain"
    if(possibleAccept.contains("text/*")) return "text/plain"
    if(possibleAccept.contains("*/*")) return "application/json"
    possibleAccept.head
  }

  def forUser(user:String) = Action { implicit request=>
    Logger.info(s"unattached atoms table is $tableName")

    val client = getClient

    val table = Table[UnattachedAtom](tableName.get)
    val userIndex = table.index("UserIndex")
    val accept=findPreferredFormat(request.headers.get("Accept").getOrElse("application/json"))

    boundsFromQueryArg(request.queryString) match {
      case Some(bounds)=>
        makeResultNew[UnattachedAtom](userIndex.query('userEmail->user and ('dateCreated between bounds)),accept){ params=>
          Scanamo.exec(client)(params)
        }
      case None=>
        makeResultNew[UnattachedAtom](userIndex.query('userEmail->user),accept) { params =>
          Scanamo.exec(client)(params)
        }
    }

  }

  def all = Action { implicit request =>
    Logger.info(s"unattached atoms table is $tableName")

    val client = getClient

    val table = Table[UnattachedAtom](tableName.get)
    val dateIndex = table.index("dateIndex")
    val accept=findPreferredFormat(request.headers.get("Accept").getOrElse("application/json"))

    boundsFromQueryArg(request.queryString) match {
      case Some(bounds)=>
        makeResultNew[UnattachedAtom](dateIndex.query('dummy->"n" and ('dateCreated between bounds)),accept) { params =>
          Scanamo.exec(client)(params)
        }
      case None=>
        makeResultNew[UnattachedAtom](null,accept) { params =>
          Scanamo.exec(client)(table.scan())
        }
    }
  }

  def getConfiguration = Action {
    val responsedata=ConfigResponse(config.get[String]("AtomToolDomain"))
    Ok(responsedata.asJson.toString)
  }
}
