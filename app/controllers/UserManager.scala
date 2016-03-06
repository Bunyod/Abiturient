package controllers

import akka.actor._
import akka.pattern.pipe
import akka.util.Timeout
import common.AppProtocol.{GeneralAuthFailure, UserAuthFailure, LoginUser}
import common.entities.{RegUser, AbUser}
import dao.UsersDao

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 11/17/15.
 */


object UserManager {
  def props(usersDao: UsersDao) =
    Props(new UserManager(usersDao))
}

class UserManager(usersDao: UsersDao) extends Actor with ActorLogging {

  log.info("Entry")
  implicit val executionContext = context.dispatcher
  implicit val defaultTimeout = Timeout(5.seconds)

  override def receive: Receive = {
    case LoginUser(login, password) =>
      checkLoginPassword(login, password).pipeTo(sender())

    case RegUser(user) =>
      log.info(s"RegUser=$user")
      register(user).pipeTo(sender())
  }

  private def register(user: AbUser): Future[Int] = {
    usersDao.create(user)
  }

  private def checkLoginPassword(login: String, password: String): Future[Either[UserAuthFailure, AbUser]] = {

    require(!login.isEmpty)

    usersDao.findByLogin(login).map(_.map { user =>
      if (user.password == password) {
        Right(user)
      } else {
        Left(GeneralAuthFailure("password does not match"))
      }
    }.getOrElse {
      Left(GeneralAuthFailure("user not found"))
    }
    )
  }
}