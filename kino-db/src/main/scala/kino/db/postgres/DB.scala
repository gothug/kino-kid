package kino.db.postgres

import java.util.Properties

import kino.db.config.Config
import kino.db.model.entity.{Film, Search, SearchResource, Subscription}
import kino.db.postgres.MyPostgresDriver.simple.Database.dynamicSession
import kino.db.postgres.MyPostgresDriver.simple._
import kino.lib.util._

import scala.slick.jdbc.StaticQuery

/**
 * @author Got Hug
 */
object DB {
  val driver = "org.postgresql.Driver"
  val name = Config.db.name
  val user = Config.db.user
  val password = Config.db.password
  val url = "jdbc:postgresql"

  val host = getEnvVar("PGSQL_PORT_5432_TCP_ADDR", "localhost")
  val port = getEnvVar("PGSQL_PORT_5432_TCP_PORT", "5432")

  val tables = List(Film.Query, Search.Query, SearchResource.Query, Subscription.Query)
  val db = Database.forURL(s"$url://$host:$port/$name", user, password, new Properties(), driver)
  val postgres = Database.forURL("jdbc:postgresql:postgres", driver = driver)

  def getDBConnection(dbname: String, port: Int = port.toInt): Database = {
    Database.forURL(s"jdbc:postgresql://localhost:$port/$dbname", user, password, new Properties,
      driver)
  }

  def create(dbname: String): Unit = {
    postgres.withDynSession {
      StaticQuery.updateNA(s"create database $dbname").execute
    }
  }

  def drop(dbname: String): Unit = {
    postgres.withDynSession {
      StaticQuery.updateNA(s"drop database if exists $dbname").execute
    }
  }

  def reCreateSchema(db: Database): Unit = {
    db.withDynSession {
      StaticQuery.updateNA(s"DROP SCHEMA PUBLIC CASCADE").execute
      StaticQuery.updateNA(s"CREATE SCHEMA PUBLIC").execute
    }
  }
}
