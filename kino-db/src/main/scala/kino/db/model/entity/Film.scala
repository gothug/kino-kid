package kino.db.model.entity

import kino.db.model.Types._
import kino.db.model.{EntityQuery, RowCount}
import kino.db.postgres.MyPostgresDriver.simple.Database.dynamicSession
import kino.db.postgres.MyPostgresDriver.simple._

import scala.util.Try

/**
 * @author Got Hug
 */
case class FilmRow(id: AutoIncrementId, title: String, year: Int)

object Film {
  val Query = TableQuery(new FilmTable(_))
}

class Film(val db: Database) extends EntityQuery[FilmRow] {
  import Film._

  def getCount: RowCount = {
    db.withDynSession {
      RowCount(Query.list.length)
    }
  }

  def fetchAll: Seq[FilmRow] = {
    db.withDynSession {
      Query.list
    }
  }

  def fetchById(id: Int): Try[FilmRow] = {
    findOne {
      Query.filter(x => x.id === id).firstOption
    }
  }

  def findFilm(f: FilmRow): Try[FilmRow] = {
    findOne {
      Query.filter(x => x.title === f.title && x.year === f.year).firstOption
    }
  }

  def addFilm(film: FilmRow): Try[FilmRow] = {
    execQuery {
      (Query returning Query.map(x => x)) += film
    }
  }
}

// scalastyle:off
class FilmTable(_tableTag: Tag) extends Table[FilmRow](_tableTag, "film") {
  def id = column[AutoIncrementId]("id", O.AutoInc, O.PrimaryKey)

  def title = column[String]("title")

  def year = column[Int]("year")

  def * = (
    id,
    title,
    year) <> (FilmRow.tupled, FilmRow.unapply)
}
