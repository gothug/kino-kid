import sbtassembly.Plugin.AssemblyKeys._

name := "kino-db"

scalaVersion := "2.11.6"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

mainClass := Some("kino.db.server.Server")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "io.spray" %% "spray-client"  % "1.3.2",
  "io.spray" %% "spray-can"     % "1.3.2",
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-testkit" % "1.3.2" % "test",
  "io.spray" %% "spray-json"    % "1.3.1",
  "io.spray" %% "spray-caching" % "1.3.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.slick"  %% "slick" % "2.1.0",
  "com.typesafe.slick"  %% "slick-codegen" % "2.1.0",
  ("com.github.tminglei" %% "slick-pg" % "0.8.2").exclude("org.slf4j", "slf4j-simple"),
  "org.liquibase" % "liquibase-core" % "2.0.5",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "default" %% "kino-kid-lib" % "0+"
)

assemblySettings

releaseSettings
