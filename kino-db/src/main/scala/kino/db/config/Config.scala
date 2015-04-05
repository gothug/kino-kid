package kino.db.config

import com.typesafe.config.ConfigFactory
import kino.db.postgres.DBConfig

/**
 * @author Got Hug
 */
object Config {
  lazy val db = DBConfig.apply(ConfigFactory.load("db"))
}
