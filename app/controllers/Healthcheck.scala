package controllers

import javax.inject._

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class Healthcheck @Inject()(cc:ControllerComponents,config:Configuration,system:ActorSystem) extends AbstractController (cc){
  def simple = Action {
    /*simplest possible healthcheck, just return a 200 response.  This app should be run with the -XX:+ExitOnOutOfMemoryError
    * option, so the JVM will terminate on an OOM error and the loadbalancer will see this*/
    Ok("online")
  }
}
