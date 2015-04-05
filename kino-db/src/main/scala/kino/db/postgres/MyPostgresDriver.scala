package kino.db.postgres

import com.github.tminglei.slickpg.{PgArraySupport, PgDateSupport, PgEnumSupport}
import kino.db.model.entity.SearchResource.SearchResourceEnum

import scala.slick.driver.PostgresDriver

object MyPostgresDriver extends PostgresDriver with PgEnumSupport
with PgArraySupport
with PgDateSupport {
  override lazy val Implicit = new Implicits with ArrayImplicits with MyEnumImplicits {}

  override val simple = new SimpleQL with MyEnumImplicits with ArrayImplicits {}

  trait MyEnumImplicits {
    implicit val resourceTypeMapper = createEnumJdbcType("Searchresource", SearchResourceEnum)
    implicit val resourceListTypeMapper =
      createEnumListJdbcType("Searchresource", SearchResourceEnum)
  }
}
