package kino.db.service

import java.util.Calendar

import kino.db.model.RowCount
import kino.db.model.entity._
import kino.db.postgres.MyPostgresDriver.simple.Database.dynamicSession
import kino.db.postgres.MyPostgresDriver.simple._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json._

import scala.slick.lifted.TableQuery

/**
 * @author Got Hug
 */
class SearchApiSpec extends ApiSpec with SearchApi {
  import kino.db.json.SearchJsonProtocol._

  val searchQuery = TableQuery(new SearchTable(_))

  def getCurTimestamp: java.sql.Timestamp = {
    new java.sql.Timestamp(Calendar.getInstance().getTime.getTime)
  }

  def addDummyFilm(): Int = {
    val filmQuery = TableQuery(new FilmTable(_))

    testDB.withDynSession {
      (filmQuery returning filmQuery.map(x => x.id.get)) += FilmRow(None, "Film1", 1991)
    }
  }

  def addDummySubscription(): Int = {
    val subscriptionQuery = TableQuery(new SubscriptionTable(_))

    testDB.withDynSession {
      (subscriptionQuery returning subscriptionQuery.map(x => x.id.get)) +=
        SubscriptionRow(None, "dummy subscription", List(), List(), List(),
          enabled = true, 5, Some(getCurTimestamp))
    }
  }

  def addSearch(subscriptionId: Int, searchResourceId: Int, filmId: Int): SearchRow = {
    val search = SearchRow(subscriptionId, searchResourceId, filmId, "search result", "searchHash",
      getCurTimestamp)

    testDB.withDynSession {
      searchQuery += search
    }

    search
  }

  "SearchApi should" should {
    "return a list of searches" in {
      val subscriptionId = addDummySubscription()
      val filmId = addDummyFilm()
      val searchResourceId = 1
      val search = addSearch(subscriptionId, searchResourceId, filmId)

      Get("/searches") ~> SearchApiRouting ~> check {
        responseAs[String] should === (Seq(search).toJson.prettyPrint)
      }
    }

    "add new search" in {
      val filmId = addDummyFilm()
      val subscriptionId = addDummySubscription()

      val search =
        SearchRow(subscriptionId, 1, filmId, "search result", "searchHash", getCurTimestamp)

      Post("/searches", search) ~> SearchApiRouting ~> check {
        status shouldBe OK
      }

      val searches = testDB.withDynSession { searchQuery.list }

      searches should === (List(search))
    }

    "return correct count of searches" in {
      import kino.db.json.RowCountJsonProtocol._

      val filmId = addDummyFilm()
      val subscriptionId = addDummySubscription()
      val searchResourceId = 1
      val search = addSearch(subscriptionId, searchResourceId, filmId)

      Get("/searches/count") ~> SearchApiRouting ~> check {
        val cnt = RowCount(1)
        responseAs[String] should === (cnt.toJson.prettyPrint)
      }
    }

    "return search by subscription/search_resource/film id" in {
      val filmId = addDummyFilm()
      val subscriptionId = addDummySubscription()
      val searchResourceId = 1
      val search = addSearch(subscriptionId, searchResourceId, filmId)

      Get(s"/search/$subscriptionId/$searchResourceId/$filmId") ~> SearchApiRouting ~> check {
        status shouldBe OK
        responseAs[String] should === (search.toJson.prettyPrint)
      }
    }

    "update search by subscription/search_resource/film id" in {
      val filmId = addDummyFilm()
      val subscriptionId = addDummySubscription()
      val searchResourceId = 1
      val search = addSearch(subscriptionId, searchResourceId, filmId)

      val searchModified = search.copy(resultHash = "searchHash_modified")

      Post(
        s"/search/$subscriptionId/$searchResourceId/$filmId", searchModified
      ) ~> SearchApiRouting ~> check {
          status shouldBe OK
      }

      val searchFromDB =
        testDB.withDynSession {
          searchQuery.filter(
            x => x.subscriptionId === subscriptionId && x.searchResourceId === searchResourceId
              && x.filmId === filmId
          ).firstOption.get
        }

      searchFromDB should === (searchModified)
    }
  }
}
