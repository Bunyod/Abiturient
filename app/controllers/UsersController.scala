package controllers

import java.util.Date
import javax.inject._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.entities.{GenderType, RegUser, User}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 11/17/15.
 */

object UsersController {
  case class RegsForm
  (
    firstName: String = "",
    lastName: String = "",
    login: String = "",
    password: String = "",
    verifyPassword: String = ""
  )

  case class LoginForm
  (
    login: String = "",
    password: String = ""
  )
}

class UsersController @Inject() (val actorSystem: ActorSystem,
                                 deadbolt: DeadboltActions,
                                 actionBuilders: ActionBuilders)
                                (implicit ec: ExecutionContext) extends Controller with LazyLogging {
  import controllers.UsersController._

  implicit val defaultTimeout = Timeout(5.seconds)
  val config = current.configuration.getConfig("web-server").get
  val myActor = actorSystem.actorSelection(config.getString("user-manager-actor-path").get)

  val regsPlayForm: Form[RegsForm] = Form {
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "login" -> nonEmptyText,
      "password" -> nonEmptyText,
      "verifyPassword" -> nonEmptyText
    )(RegsForm.apply)(RegsForm.unapply)
  }

  implicit val regsFormReads: Reads[RegsForm] = (
    (__ \ "firstName").read[String] and
    (__ \ "lastName").read[String] and
    (__ \ "login").read[String] and
    (__ \ "password").read[String] and
    (__ \ "verifyPassword").read[String]
  )(RegsForm)


  val loginPlayForm: Form[LoginForm] = Form {
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def showRegisterForm = Action {
    Ok(views.html.register(regsPlayForm))
  }

  def registration = Action(parse.form(regsPlayForm)) { implicit request =>
    val regData = request.body
    val user = User(None, Some(regData.firstName), Some(regData.lastName), Some(regData.lastName),
      regData.login, regData.password, Some(GenderType.Male), Some(new Date))
    (myActor ? RegUser(user)).mapTo[Int]
      .map { userId =>
        logger.info(s"NewUserId=$userId")
      }
    Redirect(routes.Application.loginPost())
  }

}
