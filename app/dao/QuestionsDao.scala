package dao

import javax.inject.{Inject, Singleton}

import ab.utils.Date2SqlDate
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.AppProtocol.Question
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * @author Bunyod (bunyodreal@gmail.com). Created at 2/11/16.
  */

trait QuestionsComponent
{ self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._
  class Questions(tag: Tag) extends Table[Question](tag, "Questions") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def question = column[String]("question")
    def ansA = column[String]("ansA")
    def ansB = column[String]("ansB")
    def ansC = column[String]("ansC")
    def ansD= column[String]("ansD")

    def * = (id.?, question.?, ansA.?, ansB.?, ansC.?, ansD.?) <>(Question.tupled, Question.unapply)
  }
}

@ImplementedBy(classOf[QuestionsDaoImpl])
trait QuestionsDao {
  def create(question: Question): Future[Int]
}

@Singleton
class QuestionsDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends QuestionsDao
  with QuestionsComponent
  with HasDatabaseConfigProvider[JdbcProfile]
  with Date2SqlDate
  with LazyLogging
{

  import driver.api._

  val questions = TableQuery[Questions]

  override def create(question: Question): Future[Int] = {
    logger.info(s"Dao: Creating user=$question")
    db.run(questions += question)
  }

}

