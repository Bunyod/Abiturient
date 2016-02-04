package models
import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.util.Timeout
import be.objectify.deadbolt.core.models.Subject
import common.AppProtocol.{GeneralAuthFailure, LoginUser, UserAuthFailure}
import dao.UsersDao
import play.api.libs.mailer.MailerClient
import play.api.libs.ws.WSClient
import play.libs.Scala

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 *
 * @author Bunyod Bobojonov (bunyodreal@gmail.com). Created at 11/17/15.
 */

case class User(userName: String, roles: List[SecurityRole]) extends Subject {
  def getRoles: java.util.List[SecurityRole] = Scala.asJava(roles)

  def getPermissions: java.util.List[SecurityPermission] = {
    Scala.asJava(List[SecurityPermission]())
  }

  def getIdentifier: String = userName
}

object UserAuthenticate {
  def props(mailer: MailerClient,
            ws: WSClient,
            usersDao: UsersDao) = Props(new UserAuthenticate(mailer, ws, usersDao))
}

class UserAuthenticate(val mailer: MailerClient, val ws: WSClient, val usersDao: UsersDao)
    extends Actor with ActorLogging {

  implicit val executionContext = context.dispatcher
  implicit val defaultTimeout = Timeout(5.seconds)

  override def receive: Receive = {
    case LoginUser(login, password) =>
      checkLoginPassword(login, password).pipeTo(sender())

  }

  private def checkLoginPassword(login: String, password: String): Future[Either[UserAuthFailure, String]] = {

    require(!login.isEmpty)

    usersDao.findByLogin(login).map(_.map { user =>
      if (user.password == password) {
        Right(login)
      } else {
        Left(GeneralAuthFailure("password does not match"))
      }
    }.getOrElse {
        Left(GeneralAuthFailure("user not found"))
      }
    )
  }
}