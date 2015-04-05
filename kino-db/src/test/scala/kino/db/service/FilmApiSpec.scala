package kino.db.service

import kino.db.model.RowCount
import kino.db.model.entity.{Film, FilmRow, FilmTable}
import kino.db.postgres.MyPostgresDriver.simple.Database.dynamicSession
import kino.db.postgres.MyPostgresDriver.simple._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json._

import scala.slick.lifted.TableQuery

/**
 * @author Got Hug
 */
class FilmApiSpec extends ApiSpec with FilmApi {
  import kino.db.json.FilmJsonProtocol._

  val query     = TableQuery(new FilmTable(_))
  val film = new Film(testDB)

  def addFilmToDB(title: String, year: Int): FilmRow = {
    val film = FilmRow(None, title, year)

    testDB.withDynSession {
      (query returning query.map(x => x)) += film
    }
  }

  "FilmApi" should {
    "add new film" in {
      Post("/films", FilmRow(None, "Title1", 2001)) ~> FilmApiRouting ~> check {
        status shouldBe OK
      }

      val films = testDB.withDynSession { query.list }

      films should === (List(FilmRow(Some(1), "Title1", 2001)))
    }

    "return empty list of films when the DB is empty" in {
      Get("/films") ~> FilmApiRouting ~> check {
        responseAs[String] should === ("[]")
      }
    }

    "return non-empty list of films after some films were added" in {
      val films: Seq[FilmRow] =
        Seq(("Title1", 2001), ("Title2", 2002)) map {
          x => film.addFilm(FilmRow(None, x._1, x._2)).get
        }

      Get("/films") ~> FilmApiRouting ~> check {
        responseAs[String] should === (films.toJson.prettyPrint)
      }
    }

    "return correct count of films" in {
      import kino.db.json.RowCountJsonProtocol._

      val films: Seq[FilmRow] =
        Seq(("Title1", 2001), ("Title2", 2002)) map {
          x => film.addFilm(FilmRow(None, x._1, x._2)).get
        }

      Get("/films/count") ~> FilmApiRouting ~> check {
        val cnt = RowCount(2)
        responseAs[String] should === (cnt.toJson.prettyPrint)
      }
    }

    "return correct film by id" in {
      val film1 = FilmRow(Some(1), "Title1", 2001)
      val film2 = FilmRow(Some(2), "Title2", 2002)

      testDB.withDynSession {
        query += film1
        query += film2
      }

      Get("/film/2") ~> FilmApiRouting ~> check {
        responseAs[String] should === (film2.toJson.prettyPrint)
      }
    }

    "find film" in {
      val film = addFilmToDB("Title1", 2001)

      Get("/film", film.copy(id = None)) ~> FilmApiRouting ~> check {
        status shouldBe OK
        responseAs[String] should === (film.toJson.prettyPrint)
      }
    }
  }
}
