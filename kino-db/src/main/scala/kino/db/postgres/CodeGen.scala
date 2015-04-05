package kino.db.postgres

import kino.db.config.Config

/**
 * @author Got Hug
 */
object CodeGen extends App {
  val dbname = Config.db.name
  val user = Config.db.user
  val password = Config.db.password

  val slickDriver = "scala.slick.driver.PostgresDriver"
  val jdbcDriver = "org.postgresql.Driver"
  val url = s"jdbc:postgresql://localhost/$dbname"
  val outputFolder = "/var/tmp"
  val pkg = "kino.db.model"

  scala.slick.codegen.SourceCodeGenerator.main(
    Array(slickDriver, jdbcDriver, url, outputFolder, pkg, user, password)
  )
}
