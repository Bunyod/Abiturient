package dao

import java.util.Date
import javax.inject.{Inject, Singleton}

import ab.utils.Date2SqlDate
import be.objectify.deadbolt.core.models.Subject
import com.google.inject.ImplementedBy
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.entities._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 11/17/15.
 */

trait UsersComponent
{ self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._
  class Users(tag: Tag) extends Table[AbUser](tag, "Users") with Date2SqlDate {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def secondName = column[String]("secondName")
    def login = column[String]("login")
    def password = column[String]("password")
    def gender = column[GenderType.Value]("gender",  O.Default(GenderType.Male))
    def bDay = column[Date]("bDay", O.Default(new Date()))
    def roles = column[String]("roles")

    def * = (id.?, firstName.?, lastName.?,
      secondName.?, login, password, gender.?, bDay.?, roles) <>(AbUser.tupled, AbUser.unapply)
  }
}

@ImplementedBy(classOf[UsersDaoImpl])
trait UsersDao {
  def findByLogin(login: String): Future[Option[AbUser]]
  def findRolesByUserName(userName: String): Future[Option[String]]
  def user(userName: String): Option[Subject]
  def create(user: AbUser): Future[Int]
}

@Singleton
class UsersDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends UsersDao
  with UsersComponent
  with HasDatabaseConfigProvider[JdbcProfile]
  with Date2SqlDate
  with LazyLogging
{

  import driver.api._

  val users = TableQuery[Users]

  override def create(user: AbUser): Future[Int] = {
    logger.info(s"Dao: Creating user=$user")
    logger.debug(s"Dao: Creating user=$user")
    db.run(users += user)
  }

  override def user(userName: String): Option[Subject] = {
//    Some(Subject)
    None
  }

  override def findByLogin(login: String): Future[Option[AbUser]] = {
    db.run {
      users.filter(_.login === login).result.headOption
    }
  }

  override def findRolesByUserName(userName: String): Future[Option[String]] = {
    db.run {
      users.filter(_.login === userName)
        .map(_.roles)
        .result
        .headOption
    }
  }
}

