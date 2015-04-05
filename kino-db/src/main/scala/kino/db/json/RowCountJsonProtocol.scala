package kino.db.json

import kino.db.model.RowCount
import spray.json._

/**
 * @author Got Hug
 */
object RowCountJsonProtocol extends DefaultJsonProtocol {
  implicit val CountFormat = jsonFormat1(RowCount)
}
