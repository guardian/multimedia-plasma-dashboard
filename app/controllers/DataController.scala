package controllers
import javax.inject._

import akka.actor.ActorSystem
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import com.gu.scanamo._
import com.gu.scanamo.syntax._
import models.UnattachedAtom
import play.api.Configuration
import akka.event.{DiagnosticLoggingAdapter, Logging}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{AWSCredentialsProviderChain, EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import play.api.Logger

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DataController @Inject()(cc:ControllerComponents,config:Configuration,system:ActorSystem) extends AbstractController (cc){
  private val region = config.getString("region")

  protected def getClient:AmazonDynamoDB = {
    val chain = new AWSCredentialsProviderChain(
      new EnvironmentVariableCredentialsProvider(),
      new ProfileCredentialsProvider("multimedia"),
      new ProfileCredentialsProvider(),
      InstanceProfileCredentialsProvider.getInstance()
    )

    AmazonDynamoDBClientBuilder.standard()
      .withCredentials(chain)
      .withRegion(region.getOrElse("eu-west-1"))
      .build()
  }

  def all = Action {
    val tableName:Option[String]=config.getString("UnattachedAtomsTable")

    Logger.info(s"unattached atoms table is $tableName")

    val client = getClient

    val table = Table[UnattachedAtom](tableName.get)

    val result = Scanamo.exec(client)(table.scan())
    Logger.info(s"$result")

    //https://stackoverflow.com/questions/7230999/how-to-reduce-a-seqeithera-b-to-a-eithera-seqb
    val processed_result = result collectFirst { case x@Left(_) => x } getOrElse Right(result collect {case Right(x) => x})

    processed_result match {
      case Left(error)=>
        InternalServerError(error.toString)
      case Right(atomList:List[UnattachedAtom])=>
        Ok(atomList.asJson.toString)
    }
  }
}
