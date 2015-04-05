package kino.db.service

import kino.db.actors.PostgresActor._
import kino.db.model.entity.SearchRow
import spray.httpx.SprayJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Got Hug
 */
trait SearchApi extends Api {
  import kino.db.json.SearchJsonProtocol._

  val SearchApiRouting = {
    path("searches") {
      get {
        onComplete (postgresCall(FetchAllSearches)) (pgCallResultToRoute)
      } ~
      post {
        entity(as[SearchRow]) { search =>
          onComplete (postgresCall(AddSearch(search))) (pgCallResultToRoute)
        }
      }
    } ~
    pathPrefix("searches") {
      path("count") {
        get {
          onComplete (postgresCall(GetSearchCount)) (pgCallResultToRoute)
        }
      }
    } ~
    path("search" / IntNumber / IntNumber / IntNumber) {
      (subscriptionId, searchResourceId, filmId) =>
        get {
          onComplete (
            postgresCall(FetchSearch(subscriptionId, searchResourceId, filmId))
          ) (pgCallResultToRoute)
        } ~
        post {
          entity(as[SearchRow]) { search =>
            onComplete (
              postgresCall(UpdateSearch(subscriptionId, searchResourceId, filmId, search))
            ) (pgCallResultToRoute)
          }
        }
    }
  }
}
