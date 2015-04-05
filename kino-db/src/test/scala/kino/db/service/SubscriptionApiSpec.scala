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
class SubscriptionApiSpec extends ApiSpec with SubscriptionApi {
  import kino.db.json.SubscriptionJsonProtocol._

  val subscriptionQuery = TableQuery(new SubscriptionTable(_))

  def getCurTimestamp: java.sql.Timestamp = {
    new java.sql.Timestamp(Calendar.getInstance().getTime.getTime)
  }

  def getDummySubscription(name: String): SubscriptionRow = {
    SubscriptionRow(None, name, List(), List(), List(), enabled = true, 5,
      Some(getCurTimestamp))
  }

  "SubscriptionApi should" should {
    "return a list of subscriptions" in {
      val subs =
        for {
          i <- 1 to 2
        } yield
          testDB.withDynSession {
            (subscriptionQuery returning subscriptionQuery.map(x => x)) +=
              getDummySubscription(s"name $i")
          }

      Get("/subs") ~> SubscriptionApiRouting ~> check {
        responseAs[String] should === (subs.toJson.prettyPrint)
      }
    }

    "create new subscription" in {
      val sub = getDummySubscription("dummy sub")

      Post("/subs", sub) ~> SubscriptionApiRouting ~> check {
        status shouldBe OK
      }

      val searches = testDB.withDynSession { subscriptionQuery.list }
      searches should === (List(sub.copy(id = Some(1))))
    }

    "return subscription count" in {
      import kino.db.json.RowCountJsonProtocol._

      val subs =
        for {
          i <- 1 to 2
        } yield
          testDB.withDynSession {
            (subscriptionQuery returning subscriptionQuery.map(x => x)) +=
              getDummySubscription(s"name $i")
          }

      Get("/subs/count") ~> SubscriptionApiRouting ~> check {
        val cnt = RowCount(2)
        responseAs[String] should === (cnt.toJson.prettyPrint)
      }
    }

    "return subscription by id" in {
      val sub = getDummySubscription("dummy")

      testDB.withDynSession {
        subscriptionQuery += sub
      }

      Get("/sub/1") ~> SubscriptionApiRouting ~> check {
        responseAs[String] should === (sub.copy(id = Some(1)).toJson.prettyPrint)
      }
    }

    "return 404 when no subscription exists with given id" in {
      Get("/sub/745") ~> SubscriptionApiRouting ~> check {
        status shouldBe NotFound
      }
    }

    "update existing subscription by id when record exists" in {
      val sub = getDummySubscription("dummy A")
      val subUpdated = sub.copy(name = "dummy B")

      testDB.withDynSession {
        subscriptionQuery += sub
      }

      Post("/sub/1", sub.copy(name = "dummy B")) ~> SubscriptionApiRouting ~> check {
        status shouldBe OK
      }

      val newSub =
        testDB.withDynSession {
          subscriptionQuery.filter(x => x.id === 1).firstOption.get
        }

      newSub should === (subUpdated.copy(id = Some(1)))
    }

    "return 404 when trying to update non-existing subscription" in {
      val sub = getDummySubscription("dummy")

      Post("/sub/99", sub) ~> SubscriptionApiRouting ~> check {
        status shouldBe NotFound
      }
    }

    "should not use subscription id from the subscription object json when updating" in {
      val sub = getDummySubscription("dummy")

      testDB.withDynSession {
        subscriptionQuery += sub
      }

      // id 999 in json object should not be used
      Post("/sub/1", sub.copy(id = Some(999))) ~> SubscriptionApiRouting ~> check {
        status shouldBe OK
      }

      val subFound =
        testDB.withDynSession {
          subscriptionQuery.filter(x => x.id === 1).firstOption.get
        }
      val subExpected = sub.copy(id = Some(1))

      subFound should === (subExpected)
    }
  }
}
