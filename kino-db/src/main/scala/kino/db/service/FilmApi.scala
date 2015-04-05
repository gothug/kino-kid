package kino.db.service

import kino.db.actors.PostgresActor._
import kino.db.model.entity.FilmRow
import spray.httpx.SprayJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Got Hug
 */
trait FilmApi extends Api {
  import kino.db.json.FilmJsonProtocol._

  val FilmApiRouting = {
    path("films") {
      get {
        onComplete (postgresCall(FetchAllFilms)) (pgCallResultToRoute)
      } ~
      post {
        entity(as[FilmRow]) { film =>
          onComplete (postgresCall(AddFilm(film))) (pgCallResultToRoute)
        }
      }
    } ~
    pathPrefix("films") {
      path("count") {
        get {
          onComplete (postgresCall(GetFilmCount)) (pgCallResultToRoute)
        }
      }
    } ~
    path("film" / IntNumber) { filmId =>
      get {
        onComplete (postgresCall(FetchFilm(filmId))) (pgCallResultToRoute)
      }
    } ~
    path("film") {
      get {
        entity(as[FilmRow]) { film =>
          onComplete (postgresCall(FindFilm(film))) (pgCallResultToRoute)
        }
      }
    }
  }
}
