package kino.web.service

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration._
import spray.routing._

object Service extends App with SimpleRoutingApp {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val timeout: Timeout         = Timeout(60.second)

  val Port = 50002

  startServer(interface = "0.0.0.0", port = Port) {
    path("web-fake-endpoint") {
      get {
        complete("Web Fake Endpoint Response")
      }
    }
  }
}
