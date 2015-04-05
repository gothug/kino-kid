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
case class SearchResourceRow(id: AutoIncrementId, name: SearchResourceType)

object SearchResource {
  object SearchResourceEnum extends Enumeration {
    type SearchResourceType = Value
    val Kickass, Afisha, Rutracker = Value
  }

  val Query = TableQuery(new SearchResourceTable(_))
}

class SearchResource(val db: Database) extends EntityQuery[SearchResourceRow] {
  import SearchResource._

  def getCount: RowCount = {
    db.withDynSession {
      RowCount(Query.list.length)
    }
  }

  def fetchAll: Seq[SearchResourceRow] = {
    db.withDynSession {
      Query.list
    }
  }

  def fetchById(id: Int): Try[SearchResourceRow] = {
    findOne {
      Query.filter(x => x.id === id).firstOption
    }
  }

  def addSearchResource(searchResource: SearchResourceRow): Try[SearchResourceRow] = {
    execQuery {
      (Query returning Query.map(x => x)) += searchResource
    }
  }
}

// scalastyle:off
class SearchResourceTable(_tableTag: Tag) extends Table[SearchResourceRow](_tableTag,
"search_resource") {
  def id = column[AutoIncrementId]("id", O.AutoInc, O.PrimaryKey)

  def name = column[SearchResourceType]("name")

  def * = (
    id,
    name) <> (SearchResourceRow.tupled, SearchResourceRow.unapply)
}
