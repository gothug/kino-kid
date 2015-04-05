package kino.db.service

import akka.actor.Props
import akka.util.Timeout
import kino.db.PostgresSpec
import kino.db.actors.PostgresActor
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import spray.testkit.ScalatestRouteTest

import scala.concurrent.duration._

/**
 * @author Got Hug
 */
trait ApiSpec extends WordSpec with ScalatestRouteTest with Matchers
with TypeCheckedTripleEquals with BeforeAndAfter with PostgresSpec {
  def actorRefFactory = system

  implicit val timeout: Timeout = Timeout(60, SECONDS)
  implicit val routeTestTimeout = RouteTestTimeout(5.second)

  implicit val postgresWorker = system.actorOf(Props(classOf[PostgresActor], testDB), "postgres-worker")
}
