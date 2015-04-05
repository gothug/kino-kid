package kino.db.json

import kino.db.model.entity.SearchRow
import spray.json._

/**
 * @author Got Hug
 */
object SearchJsonProtocol extends DefaultJsonProtocol {
  import kino.db.json.TimestampTypeJsonProtocol._

  implicit val SearchFormat = jsonFormat6(SearchRow)
}
