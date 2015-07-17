name := "kino-web"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

mainClass := Some("kino.web.service.Service")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "io.spray" %% "spray-client"  % "1.3.2",
  "io.spray" %% "spray-can"     % "1.3.2",
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-testkit" % "1.3.2" % "test",
  "io.spray" %% "spray-json"    % "1.3.1",
  "io.spray" %% "spray-caching" % "1.3.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "default" %% "kino-kid-lib" % "0+"
)
