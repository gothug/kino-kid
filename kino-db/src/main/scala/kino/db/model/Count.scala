package kino.db.model

import spray.json.DefaultJsonProtocol

/**
 * @author Got Hug
 */
package object count {
  case class Count(count: Int)

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val CountFormat = jsonFormat1(Count)
  }
}
