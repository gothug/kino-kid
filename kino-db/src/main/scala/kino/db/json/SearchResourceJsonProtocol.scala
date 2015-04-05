package kino.db.json

import kino.db.model.entity.SearchResourceRow
import spray.json._

/**
 * @author Got Hug
 */
object SearchResourceJsonProtocol extends DefaultJsonProtocol {
  import kino.db.json.SearchResourceTypeJsonProtocol._

  implicit val SearchResourceFormat = jsonFormat2(SearchResourceRow)
}
