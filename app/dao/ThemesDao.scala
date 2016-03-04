package dao

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.AppProtocol.Theme
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * @author Bunyod (bunyodreal@gmail.com). Created at 04/03/16.
  */

trait ThemesComponent extends SubjectsComponent
{ self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._
  class Themes(tag: Tag) extends Table[Theme](tag, "Themes") {
    val subjects = TableQuery[Subjects]

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def subjectId = column[Int]("subjectId")
    def name = column[String]("name")

    def * = (id.?, subjectId.?, name.?) <>(Theme.tupled, Theme.unapply)

    def subject = foreignKey("themesFkSubjectId", subjectId, subjects)(_.id)

  }
}

@ImplementedBy(classOf[ThemesDaoImpl])
trait ThemesDao {
  def create(theme: Theme): Future[Int]
  def getThemes(): Future[Seq[Theme]]
}

@Singleton
class ThemesDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends ThemesDao
  with ThemesComponent
  with HasDatabaseConfigProvider[JdbcProfile]
  with LazyLogging
{

  import driver.api._

  val themes = TableQuery[Themes]

  override def create(theme: Theme): Future[Int] = {
    logger.info(s"Dao: Creating theme=$theme")
    db.run(themes += theme)
  }

  override def getThemes(): Future[Seq[Theme]] = {
    db.run {
      themes.result
    }
  }

}

