package kino.db.model.entity

import kino.db.model.{EntityQuery, RowCount}
import kino.db.postgres.MyPostgresDriver.simple.Database.dynamicSession
import kino.db.postgres.MyPostgresDriver.simple._

import scala.util.Try

/**
 * @author Got Hug
 */
case class SearchRow(subscriptionId: Int, searchResourceId: Int, filmId: Int, result: String,
                     resultHash: String, lastUpdate: java.sql.Timestamp)

object Search {
  val Query = TableQuery(new SearchTable(_))
}

class Search(val db: Database) extends EntityQuery[SearchRow] {
  import Search._

  def getCount: RowCount = {
    db.withDynSession {
      RowCount(Query.list.length)
    }
  }

  def fetchAll: Seq[SearchRow] = {
    db.withDynSession {
      Query.list
    }
  }

  def fetchSearch(subId: Int, resId: Int, filmId: Int): Try[SearchRow] = {
    findOne {
      Query.filter(
        x => x.subscriptionId === subId && x.searchResourceId === resId && x.filmId === filmId
      ).firstOption
    }
  }

  def addSearch(search: SearchRow): Try[SearchRow] = {
    execQuery {
      (Query returning Query.map(x => x)) += search
    }
  }

  def updateSearch(subId: Int, resId: Int, filmId: Int, search: SearchRow): Int = {
    db.withDynSession {
      val updateQuery =
        for {
          s <- Query
            if s.subscriptionId === subId && s.searchResourceId === resId && s.filmId === filmId
        } yield s

      updateQuery.update(
        search.copy(subscriptionId = subId, searchResourceId = resId, filmId = filmId))
    }
  }
}

// scalastyle:off
class SearchTable(_tableTag: Tag) extends Table[SearchRow](_tableTag, "search") {
  def subscriptionId = column[Int]("subscription_id")

  def searchResourceId = column[Int]("search_resource_id")

  def filmId = column[Int]("film_id")

  def result = column[String]("result")

  def resultHash = column[String]("result_hash")

  def lastUpdate = column[java.sql.Timestamp]("last_update")

  def * = (
    subscriptionId,
    searchResourceId,
    filmId,
    result,
    resultHash,
    lastUpdate) <> (SearchRow.tupled, SearchRow.unapply)
}
