package kino.db.json

import kino.db.model.entity.SubscriptionRow
import spray.json._

/**
 * @author Got Hug
 */
object SubscriptionJsonProtocol extends DefaultJsonProtocol {
  import kino.db.json.SearchResourceTypeJsonProtocol._
  import kino.db.json.TimestampTypeJsonProtocol._

  implicit val SubscriptionFormat = jsonFormat8(SubscriptionRow)
}

