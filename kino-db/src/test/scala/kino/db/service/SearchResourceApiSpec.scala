package kino.db.service

import kino.db.model.entity.SearchResource.SearchResourceEnum._
import kino.db.model.entity._
import spray.httpx.SprayJsonSupport._
import spray.json._

import scala.slick.lifted.TableQuery

/**
 * @author Got Hug
 */
class SearchResourceApiSpec extends ApiSpec with SearchResourceApi {
  import kino.db.json.SearchResourceJsonProtocol._

  val searchResourceQuery = TableQuery(new SearchResourceTable(_))

  "SearchResourceApi should" should {
    "return a list of search resources" in {
      Get("/sresources") ~> SearchResourceApiRouting ~> check {
        val resources = Seq(
          SearchResourceRow(Some(1), Kickass),
          SearchResourceRow(Some(2), Afisha),
          SearchResourceRow(Some(3), Rutracker))

        responseAs[String] should === (resources.toJson.prettyPrint)
      }
    }

    "return search resource by id" in {
      Get("/sresource/1") ~> SearchResourceApiRouting ~> check {
        val kickassResource = SearchResourceRow(Some(1), Kickass)
        responseAs[String] should === (kickassResource.toJson.prettyPrint)
      }
    }
  }
}
