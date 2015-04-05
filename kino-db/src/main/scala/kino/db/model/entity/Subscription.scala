package kino.db.model.entity

import kino.db.model.Types._
import kino.db.model.entity.SearchResource.SearchResourceEnum.SearchResourceType
import kino.db.model.{EntityQuery, RowCount}
import kino.db.postgres.MyPostgresDriver.simple.Database.dynamicSession
import kino.db.postgres.MyPostgresDriver.simple._

import scala.util.Try

/**
 * @author Got Hug
 */
case class SubscriptionRow(id: AutoIncrementId, name: String, watchlists: List[String],
                        searchResources: List[SearchResourceType], emails: List[String],
                        enabled: Boolean, scheduleIntervalSeconds: Int,
                        lastSent: Option[java.sql.Timestamp])

object Subscription {
  val Query = TableQuery(new SubscriptionTable(_))
}

class Subscription(val db: Database) extends EntityQuery[SubscriptionRow] {
  import Subscription._

  def getCount: RowCount = {
    db.withDynSession {
      RowCount(Query.list.length)
    }
  }

  def fetchAll: Seq[SubscriptionRow] = {
    db.withDynSession {
      Query.list
    }
  }

  def fetchById(id: Int): Try[SubscriptionRow] = {
    findOne {
      Query.filter(x => x.id === id).firstOption
    }
  }

  def addSubscription(subscription: SubscriptionRow): Try[SubscriptionRow] = {
    execQuery {
      (Query returning Query.map(x => x)) += subscription
    }
  }

  def updateSubscription(subId: Int, subscription: SubscriptionRow): Int = {
    db.withDynSession {
      val updateQuery = for { s <- Query if s.id === subId } yield s
      updateQuery.update(subscription.copy(id = Some(subId)))
    }
  }
}

// scalastyle:off
class SubscriptionTable(_tableTag: Tag) extends Table[SubscriptionRow](_tableTag, "subscription") {
  def id = column[AutoIncrementId]("id", O.AutoInc, O.PrimaryKey)

  def name = column[String]("name")

  def watchlists = column[List[String]]("watchlists")

  def searchResources = column[List[SearchResourceType]]("search_resources")

  def emails = column[List[String]]("emails")

  def enabled = column[Boolean]("enabled")

  def scheduleIntervalSeconds = column[Int]("schedule_interval_seconds")

  def lastSent = column[Option[java.sql.Timestamp]]("last_sent")

  def * = (
    id,
    name,
    watchlists,
    searchResources,
    emails,
    enabled,
    scheduleIntervalSeconds,
    lastSent) <> (SubscriptionRow.tupled, SubscriptionRow.unapply)
}
