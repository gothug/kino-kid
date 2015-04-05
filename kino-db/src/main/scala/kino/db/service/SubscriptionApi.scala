package kino.db.service

import kino.db.actors.PostgresActor._
import kino.db.model.entity.SubscriptionRow
import spray.httpx.SprayJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Got Hug
 */
trait SubscriptionApi extends Api {
  import kino.db.json.SubscriptionJsonProtocol._

  val SubscriptionApiRouting = {
    path("subs") {
      get {
        onComplete (postgresCall(FetchAllSubscriptions)) (pgCallResultToRoute)
      } ~
      post {
        entity(as[SubscriptionRow]) { subscription =>
          onComplete (postgresCall(AddSubscription(subscription))) (pgCallResultToRoute)
        }
      }
    } ~
    pathPrefix("subs") {
      path("count") {
        get {
          onComplete (postgresCall(GetSubscriptionCount)) (pgCallResultToRoute)
        }
      }
    } ~
    path("sub" / IntNumber) { subscriptionId =>
      get {
        onComplete (postgresCall(FetchSubscription(subscriptionId))) (pgCallResultToRoute)
      } ~
      post {
        entity(as[SubscriptionRow]) { subscription =>
          onComplete (
            postgresCall(UpdateSubscription(subscriptionId, subscription))) (pgCallResultToRoute)
        }
      }
    }
  }
}
