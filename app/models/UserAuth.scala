package models
import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.util.Timeout
import be.objectify.deadbolt.core.models.Subject
import common.AppProtocol.{GeneralAuthFailure, LoginUser, UserAuthFailure}
import common.entities.User
import dao.UsersDao
import play.api.libs.mailer.MailerClient
import play.api.libs.ws.WSClient
import play.libs.Scala

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
/**
 *
 * @author Bunyod Bobojonov (bunyodreal@gmail.com)
 */

case class UserAuth(userName: String) extends Subject {
  def getRoles: java.util.List[SecurityRole] = {
    Scala.asJava(List(SecurityRole("ADMIN"),
                      SecurityRole("USER"),
                      SecurityRole("ADMINISTATOR")))
  }

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

  def checkLoginPassword(login: String, password: String): Future[Either[UserAuthFailure, User]] = {
    require(!login.isEmpty)
    (for {
      user <- usersDao.findByLogin(login)
    } yield user)
      .map {
      case user =>
        if (user.isDefined) {
          if (user.get.password == password) {
            Right(user.get)
          } else {
            Left(GeneralAuthFailure("password does not match"))
          }
        } else {
          Left(GeneralAuthFailure("user not found"))
        }
      case _ =>
        Left(GeneralAuthFailure("user not found"))
    }
  }
}
//class User @Inject() (usersDao: UsersDao) {
//  import play.api.Play
//  private lazy val adminUsername = Play.current.configuration.getString("login.username").getOrElse("")
//  private lazy val adminPassword = Play.current.configuration.getString("login.password").getOrElse("")
//
//  def authenticate(username: String, password: String): Option[UserAuth] =
//    if ((adminUsername == username) && (adminPassword == password))
//      Some(UserAuth(username))
//    else
//      None
//}