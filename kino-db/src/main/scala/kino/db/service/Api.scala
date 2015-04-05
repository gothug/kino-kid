package kino.db.service

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import spray.routing
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * @author Got Hug
 */
trait Api extends HttpService {
  val notfoundHttpCode = 404
  val conflictHttpCode = 409

  implicit val postgresWorker: ActorRef
  implicit val timeout: Timeout

  def postgresCall(message: Any)(implicit postgresWorker: ActorRef): Future[String] =
    (postgresWorker ? message).mapTo[Try[String]].map {
      case Success(result) => result
      case Failure(error) => throw error
    }

  def pgCallResultToRoute(pgCallResponse: Try[String]): routing.Route = pgCallResponse match {
    case Success(response) =>
      complete(response)
    case Failure(error)    =>
      if (error.getMessage.startsWith("ERROR: duplicate key")) {
        complete(conflictHttpCode, error.getMessage)
      } else if (error.getMessage.startsWith("ERROR: not found")) {
        complete(notfoundHttpCode, "")
      } else {
        complete(error)
      }
  }
}
