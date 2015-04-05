package kino.db.service

import kino.db.actors.PostgresActor._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Got Hug
 */
trait SearchResourceApi extends Api {

  val SearchResourceApiRouting = {
    path("sresources") {
      get {
        onComplete (postgresCall(FetchAllSearchResources)) (pgCallResultToRoute)
      }
    } ~
    pathPrefix("sresources") {
      path("count") {
        get {
          onComplete (postgresCall(GetSearchResourceCount)) (pgCallResultToRoute)
        }
      }
    } ~
    path("sresource" / IntNumber) { searchResourceId =>
      get {
        onComplete (postgresCall(FetchSearchResource(searchResourceId))) (pgCallResultToRoute)
      }
    }
  }
}
