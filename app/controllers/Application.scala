package controllers

import javax.inject.{Singleton, Inject}

import akka.actor.ActorSystem
import akka.util.Timeout
import dao.UsersDao
import play.api.Logger
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

/**
 * Created by bunyod on 11/17/15.
 */

@Singleton
class Application @Inject() (actorSystem: ActorSystem, usersDao: UsersDao)
                           (implicit ec: ExecutionContext) extends Controller {
  implicit val defaultTimeout = Timeout(5.seconds)
//  val config = current.configuration.getConfig("web-server").get
  val myActor = actorSystem.actorOf(MyActor.props(usersDao), "user-manager")

  val logger = Logger(this.getClass())

  logger.info("In AppController")
  def index = Action {
    Redirect(routes.UsersController.showRegisterForm())
  }


}