package security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import dao.UsersDao
import models.{SecurityRole, User}
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None, usersDao: UsersDao) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    Future(dynamicResourceHandler.orElse(Some(new MyDynamicResourceHandler())))
  }

  override def getSubject[A](request: Request[A]): Future[Option[Subject]] = {
    // e.g. request.session.get("user")
      request.session.get("ab-user") match {
        case Some(username) =>
          val roles = usersDao.findRolesByUserName(username)
          roles.map { r =>
            val ss = r.map { t =>
              List(SecurityRole(t))
            }.getOrElse(List(SecurityRole("")))

            Some(User(username, ss))
          }

        case None => Future(None)
      }
  }

  def onAuthFailure[A](request: Request[A]): Future[Result] = {
    Future {Results.Forbidden(views.html.authFailed())}
  }
}