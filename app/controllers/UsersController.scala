package controllers

import java.util.Date
import javax.inject._

import ab.utils.JsonFormatUtils._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.entities.{AbUser, GenderType, RegUser}
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
    firstName: Option[String] = None,
    lastName: Option[String] = None,
    email: String,
    password: String,
    confirmPassword: String,
    birthDate: Option[Date],
    gender: String
  )

  implicit val regsFormReads: Reads[RegsForm] = (
    optStringRead("firstName") and
      optStringRead("lastName") and
      (__ \ "email").read [String] and
      (__ \ "password").read [String] and
      (__ \ "confirmPassword").read [String] and
      optDateTimeFormat("birthDate") and
      (__ \ "gender").read [String]
    )(RegsForm)

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
  val userManger = actorSystem.actorSelection(config.getString("user-manager-actor-path").get)


  val loginPlayForm: Form[LoginForm] = Form {
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  }

  def registration = Action.async(parse.json[RegsForm]) { implicit request =>
    val regData = request.body
    val user = AbUser(None, regData.firstName, regData.lastName, regData.lastName,
      regData.email, regData.password, Some(GenderType.Male), Some(new Date), "USER")

    (userManger ? RegUser(user)).mapTo[Int].map { userId =>
      logger.info(s"NewUserId=$userId")
      Ok(Json.toJson(userId))
    }
  }

}
