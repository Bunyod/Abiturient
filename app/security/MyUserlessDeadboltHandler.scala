package security

import be.objectify.deadbolt.core.models.Subject
import dao.UsersDao
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

class MyUserlessDeadboltHandler(usersDao: UsersDao) extends MyDeadboltHandler(None, usersDao)
{
  override def getSubject[A](request: Request[A]): Future[Option[Subject]] = Future(None)
}