package kino.mailer.service

import akka.actor.ActorSystem
import akka.util.Timeout
import spray.client.pipelining._
import spray.http.{HttpResponse, HttpRequest}

import scala.concurrent.Future
import scala.concurrent.duration._
import spray.routing._

object Service extends App with SimpleRoutingApp {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val timeout: Timeout         = Timeout(60.second)

  val Port = 8080

  startServer(interface = "0.0.0.0", port = Port) {
    import actorSystem.dispatcher

    path("send-mails") {
      get {
        complete {
          def queryKinoWebService(): Future[HttpResponse] = {
            val KinoWebPort = 50002

            val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
            val url = s"http://localhost:$KinoWebPort/web-fake-endpoint"
            pipeline(Get(url))
          }

          def queryKinoDBService(): Future[HttpResponse] = {
            val KinoDBPort = 50001

            val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
            val url = s"http://localhost:$KinoDBPort/sresources"
            pipeline(Get(url))
          }


          val webRespFuture = queryKinoWebService()
          val dbRespFuture = queryKinoDBService()

          val response =
            for {
              r1 <- webRespFuture
              r2 <- dbRespFuture
            } yield Seq(r1, r2).map(_.entity.asString).mkString("\n\n")

          response
//          webRespFuture.map(_.entity.asString)
        }
      }
    }
  }
}
