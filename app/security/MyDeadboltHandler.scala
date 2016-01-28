package security

import be.objectify.deadbolt.core.models.Subject
import be.objectify.deadbolt.scala.{DeadboltHandler, DynamicResourceHandler}
import models.UserAuth
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class MyDeadboltHandler(dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    Future(dynamicResourceHandler.orElse(Some(new MyDynamicResourceHandler())))
  }

  override def getSubject[A](request: Request[A]): Future[Option[Subject]] = {
    // e.g. request.session.get("user")
    Future {
      request.session.get("ab-user") match {
        case Some(username) =>
          Some(UserAuth(username))
        case None => None
      }
    }
  }

  def onAuthFailure[A](request: Request[A]): Future[Result] = {
    Future {Results.Forbidden(views.html.accessFailed())}
  }
}