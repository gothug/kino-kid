package kino.db

import kino.db.postgres.{DB, LiquiBase}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Suite}

/**
 * @author Got Hug
 */
trait PostgresSpec extends Suite with BeforeAndAfterAll with BeforeAndAfter {
  val testDBName = getClass.getSimpleName.toLowerCase
  val testDB = DB.getDBConnection(testDBName)

  override def beforeAll(): Unit = {
    DB.drop(testDBName)
    DB.create(testDBName)
  }

  override def afterAll(): Unit = {
    DB.drop(testDBName)
  }

  before {
    DB.reCreateSchema(testDB)
    LiquiBase.update(testDBName, tests = true)
  }
}
