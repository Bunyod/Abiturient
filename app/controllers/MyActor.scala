package controllers

import akka.actor._
import akka.pattern.pipe
import akka.util.Timeout
import common.entities.{RegUser, User}
import dao.UsersDao

import scala.concurrent.duration.DurationInt

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 11/17/15.
 */


object MyActor {
  def props(usersDao: UsersDao) =
    Props(new MyActor(usersDao))
}

class MyActor (usersDao: UsersDao) extends Actor with ActorLogging {
  log.info("Entry")
  implicit val executionContext = context.dispatcher
  implicit val defaultTimeout = Timeout(5.seconds)
  override def receive: Receive = {
    case RegUser(user) =>
      register(user).pipeTo(sender)
    case _ =>
      log.info(s"Receive: None")
  }

  private def register(user: User) = {
    usersDao.create(user)
  }
}