package kino.db.actors

import akka.actor.Actor
import kino.db.model.RowCount
import kino.db.model.entity._
import kino.db.postgres.MyPostgresDriver.simple._
import spray.json._

import scala.util.{Failure, Success, Try}

/**
 * @author Got Hug
 */
object PostgresActor {
  case object FetchAllFilms
  case class  AddFilm(film: FilmRow)
  case class  FetchFilm(id: Int)
  case class  FindFilm(film: FilmRow)
  case object GetFilmCount

  case object FetchAllSubscriptions
  case class  AddSubscription(subscription: SubscriptionRow)
  case class  UpdateSubscription(id: Int, subscription: SubscriptionRow)
  case class  FetchSubscription(id: Int)
  case object GetSubscriptionCount

  case object FetchAllSearchResources
  case class  AddSearchResource(searchResource: SearchResourceRow)
  case class  FetchSearchResource(id: Int)
  case object GetSearchResourceCount

  case object FetchAllSearches
  case class  FetchSearch(subscriptionId: Int, searchResourceId: Int, filmId: Int)
  case class  AddSearch(search: SearchRow)
  case class  UpdateSearch(subscriptionId: Int, searchResourceId: Int, filmId: Int,
                           search: SearchRow)
  case object GetSearchCount
}

class PostgresActor(val db: Database) extends Actor {
  import PostgresActor._

  def receive: Receive = filmPkg.filmReceive orElse subsPkg.subsReceive orElse
    searchResourcePkg.sresourceReceive orElse searchPkg.searchReceive

  object filmPkg {
    import kino.db.json.FilmJsonProtocol._

    val film = new Film(db)

    def filmReceive: Receive = {
      case FetchAllFilms =>
        val films: Seq[FilmRow] = film.fetchAll
        sender ! Success(films.toJson.prettyPrint)

      case GetFilmCount =>
        import kino.db.json.RowCountJsonProtocol._
        val filmCount: RowCount = film.getCount
        sender ! Success(filmCount.toJson.prettyPrint)

      case FetchFilm(id: Int) =>
      val rowFetched: Try[FilmRow] = film.fetchById(id)
      sender ! rowFetched.map(_.toJson.prettyPrint)

      case FindFilm(f: FilmRow) =>
        val rowFound: Try[FilmRow] = film.findFilm(f)
        sender ! rowFound.map(_.toJson.prettyPrint)

      case AddFilm(f: FilmRow) =>
      val filmInserted: Try[FilmRow] = film.addFilm(f)
      sender ! filmInserted.map(_.toJson.prettyPrint)
    }
  }

  object subsPkg {
    import kino.db.json.SubscriptionJsonProtocol._

    val sub = new Subscription(db)

    def subsReceive: Receive = {
      case FetchAllSubscriptions =>
        val subscriptions: Seq[SubscriptionRow] = sub.fetchAll
        sender ! Success(subscriptions.toJson.prettyPrint)

      case GetSubscriptionCount =>
        import kino.db.json.RowCountJsonProtocol._
        val subscriptionCount: RowCount = sub.getCount
        sender ! Success(subscriptionCount.toJson.prettyPrint)

      case FetchSubscription(id: Int) =>
        val rowFetched: Try[SubscriptionRow] = sub.fetchById(id)
        sender ! rowFetched.map(_.toJson.prettyPrint)

      case AddSubscription(subscription: SubscriptionRow) =>
        val subscriptionInserted: Try[SubscriptionRow] = sub.addSubscription(subscription)
        sender ! subscriptionInserted.map(_.toJson.prettyPrint)

      case UpdateSubscription(id: Int, subscription: SubscriptionRow) =>
        val updateCount: Int = sub.updateSubscription(id, subscription)
        if (updateCount == 0) {
          sender ! Failure(new Exception("ERROR: not found"))
        } else {
          sender ! Success("")
        }
    }
  }

  object searchResourcePkg {
    import kino.db.json.SearchResourceJsonProtocol._

    val resource = new SearchResource(db)

    def sresourceReceive: Receive = {
      case FetchAllSearchResources =>
        val searchResources: Seq[SearchResourceRow] = resource.fetchAll
        sender ! Success(searchResources.toJson.prettyPrint)

      case GetSearchResourceCount =>
        import kino.db.json.RowCountJsonProtocol._
        val cnt: RowCount = resource.getCount
        sender ! Success(cnt.toJson.prettyPrint)

      case FetchSearchResource(id: Int) =>
        val rowFetched: Try[SearchResourceRow] = resource.fetchById(id)
        sender ! rowFetched.map(_.toJson.prettyPrint)

      case AddSearchResource(searchResource: SearchResourceRow) =>
        val rowInserted: Try[SearchResourceRow] = resource.addSearchResource(searchResource)
        sender ! rowInserted.map(_.toJson.prettyPrint)
    }
  }

  object searchPkg {
    import kino.db.json.SearchJsonProtocol._

    val search = new Search(db)

    def searchReceive: Receive = {
      case FetchAllSearches =>
        val searches: Seq[SearchRow] = search.fetchAll
        sender ! Success(searches.toJson.prettyPrint)

      case FetchSearch(subId, resId, filmId) =>
        val rowFetched: Try[SearchRow] = search.fetchSearch(subId, resId, filmId)
        sender ! rowFetched.map(_.toJson.prettyPrint)

      case GetSearchCount =>
        import kino.db.json.RowCountJsonProtocol._
        val cnt: RowCount = search.getCount
        sender ! Success(cnt.toJson.prettyPrint)

      case AddSearch(s: SearchRow) =>
        val rowInserted: Try[SearchRow] = search.addSearch(s)
        sender ! rowInserted.map(_.toJson.prettyPrint)

      case UpdateSearch(subId, resId, filmId, s: SearchRow) =>
        val updateCount: Int = search.updateSearch(subId, resId, filmId, s)
        if (updateCount == 0) {
          sender ! Failure(new Exception("ERROR: not found"))
        } else {
          sender ! Success("")
        }
    }
  }
}
