package kino.db.service

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.Logger
import kino.db.actors.PostgresActor
import kino.db.postgres.DB
import org.slf4j.LoggerFactory
import spray.routing.SimpleRoutingApp

import scala.concurrent.duration._

object Service extends App with FilmApi with SubscriptionApi with SearchResourceApi with SearchApi
with SimpleRoutingApp {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val timeout: Timeout         = Timeout(60.second)

  implicit val postgresWorker = actorSystem.actorOf(Props(classOf[PostgresActor], DB.db),
    "postgres-worker")

  val logger = Logger(LoggerFactory.getLogger("default"))

  val Port = 50001

  startServer(interface = "0.0.0.0", port = Port) {
    FilmApiRouting ~
    SubscriptionApiRouting ~
    SearchResourceApiRouting ~
    SearchApiRouting ~
    path("version") {
      get {
        complete(sbtbuildinfo.BuildInfo.version)
      }
    }
  }
}
