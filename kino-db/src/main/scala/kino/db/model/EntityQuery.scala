package kino.db.model

import kino.db.postgres.MyPostgresDriver.simple._

import scala.util.Try

/**
 * @author Got Hug
 */
trait EntityQuery[T] {
  val db: Database

  def findOne(query: =>Option[T]): Try[T] = {
    db.withDynSession {
      Try {
        query match {
          case Some(row) => row
          case None => throw new Exception("ERROR: not found")
        }
      }
    }
  }

  def execQuery(query: =>T): Try[T] = {
    db.withDynSession {
      Try(query)
    }
  }
}
