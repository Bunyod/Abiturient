package dao

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.AppProtocol.Subject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * @author Bunyod (bunyodreal@gmail.com). Created at 04/03/16.
  */

trait SubjectsComponent
{ self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._
  class Subjects(tag: Tag) extends Table[Subject](tag, "Subjuects") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id.?, name) <>(Subject.tupled, Subject.unapply)
  }
}

@ImplementedBy(classOf[QuestionsDaoImpl])
trait SubjectsDao {
  def create(subject: Subject): Future[Int]
  def getSubjects(): Future[Seq[Subject]]
}

@Singleton
class SubjectsDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends SubjectsDao
  with SubjectsComponent
  with HasDatabaseConfigProvider[JdbcProfile]
  with LazyLogging
{

  import driver.api._

  val subjects = TableQuery[Subjects]

  override def create(subject: Subject): Future[Int] = {
    logger.info(s"Dao: Creating subject=$subject")
    db.run(subjects += subject)
  }

  override def getSubjects(): Future[Seq[Subject]] = {
    db.run {
      subjects.result
    }
  }

}

