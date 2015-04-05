package kino.db.postgres

import com.typesafe.scalalogging.Logger
import kino.db.config.Config
import liquibase.Liquibase
import liquibase.integration.commandline.CommandLineUtils
import liquibase.logging.{LogFactory, LogLevel}
import liquibase.resource.FileSystemResourceAccessor
import org.slf4j.LoggerFactory

/**
* @author Got Hug
*/
object LiquiBase extends App {
  val logger = Logger(LoggerFactory.getLogger("LiquiBase"))

  if (args.contains("create")) {
    init(args.contains("drop"))
    update(Config.db.name)
  }

  if (args.contains("update")) {
    update(Config.db.name)
  }

  def init(drop: Boolean = false) {
    if (drop) {
      DB.drop(Config.db.name)
      logger.info("DB dropped") // scalastyle:ignore
    }
    DB.create(Config.db.name)
    logger.info("DB created") // scalastyle:ignore
  }

  def update(dbName: String, tests: Boolean = false) {
    def runMigration(file: String): Unit = {
      val uname = Config.db.user
      val pass = Config.db.password
      val driver = Config.db.driver
      val url = "jdbc:postgresql:" + dbName
      val loader = this.getClass.getClassLoader

      val database = CommandLineUtils.createDatabaseObject(loader, url, uname, pass, driver, null, // scalastyle:ignore
        null, null) // scalastyle:ignore
      val liquibase = new Liquibase(file, new FileSystemResourceAccessor, database)

      liquibase.update("")

      database.close()
    }

    val dbChangeLogPath = getClass.getResource("/migrations/db-changelog.xml")

    if (tests) {
      LogFactory.getLogger.setLogLevel(LogLevel.SEVERE)
    } else {
      LogFactory.getLogger.setLogLevel(LogLevel.INFO)
    }

    runMigration(dbChangeLogPath.getFile)

    if (!tests) {
      logger.info("DB updated") // scalastyle:ignore
    }
  }
}
