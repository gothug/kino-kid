package kino.db.json

import kino.db.model.entity.SearchResource.SearchResourceEnum
import kino.db.model.entity.SearchResource.SearchResourceEnum.SearchResourceType
import spray.json._

/**
 * @author Got Hug
 */
object SearchResourceTypeJsonProtocol {
  implicit object SearchResourceTypeFormat extends RootJsonFormat[SearchResourceType] {
    def write(obj: SearchResourceType): JsValue = JsString(obj.toString)

    def read(json: JsValue): SearchResourceType = json match {
      case JsString(str) => SearchResourceEnum.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }
}
