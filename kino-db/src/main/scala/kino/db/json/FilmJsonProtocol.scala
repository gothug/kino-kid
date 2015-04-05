package kino.db.json

import kino.db.model.entity.FilmRow
import spray.json._

/**
 * @author Got Hug
 */
object FilmJsonProtocol extends DefaultJsonProtocol {
  implicit val FilmFormat = jsonFormat3(FilmRow)
}

