package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.util.Timeout
import be.objectify.deadbolt.scala.DeadboltActions
import common.entities.SessionUser
import dao.UsersDao
import play.api.Play.current
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc._
import security.MyDeadboltHandler

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 11/17/15.
 */


@Singleton
class Application @Inject() (val messagesApi: MessagesApi, actorSystem: ActorSystem, usersDao: UsersDao, deadbolt: DeadboltActions)
                           (implicit ec: ExecutionContext) extends Controller {
  implicit val defaultTimeout = Timeout(5.seconds)
  //  val config = current.configuration.getConfig("web-server").get
  val myActor = actorSystem.actorOf(MyActor.props(usersDao), "user-manager")

  val logger = Logger(this.getClass())

  logger.info("In AppController")
  implicit val sessionUserFormat = Json.format[SessionUser]

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> text)
      verifying("Invalid username or password", result => result match {
      case (username, password) =>
        models.User.authenticate(username, password).isDefined
    }))

  def index = Action {
    //    Redirect(routes.UsersController.showRegisterForm())
    Ok(views.html.index(loginForm))
  }

  def login = {
    Action { implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => { // binding failure
          Logger.error("Login failed for user " + formWithErrors.data("username"))
          BadRequest(views.html.index(formWithErrors))
        },
        user => {
          // update the session BEFORE the view is rendered
//          val modifiedRequest = updateRequestSession(request, List(("user" -> user._1)))
          Ok(views.html.pageA(request)).withSession("user" -> Json.prettyPrint(Json.toJson(SessionUser(user._1, user._2))))
        })
    }
  }



  private def updateRequestSession(request: Request[Any], additionalSessionParams: List[(String, String)]): Request[Any] = {
    import scala.language.reflectiveCalls

    val updatedSession = additionalSessionParams.foldLeft[Session](request.session) { _ + _ }
    val existingSession = request.headers.get(SET_COOKIE).map(cookies => Session.decodeFromCookie(Cookies.decodeCookieHeader(cookies).find(_.name == Session.COOKIE_NAME)))
    val newSession = if (existingSession.isDefined)
      existingSession.get.data.foldLeft(updatedSession) { _ + _ }
    else
      updatedSession

    val cookies = Cookies.decodeCookieHeader(request.headers.get(COOKIE).get)
    val myCookies = Seq(Session.COOKIE_NAME -> Session.encodeAsCookie(newSession))
    val headerMap = request.headers.toMap +
      (COOKIE -> Seq(Cookies.encodeSetCookieHeader(myCookies.map(_._2))))
    var tmpHeaderList: ListBuffer[Tuple2[String, String]] = new ListBuffer[(String, String)]();
    for((k,v) <- headerMap){
      tmpHeaderList += new Tuple2(k, v.head)
    }
    val theHeaders = Headers(tmpHeaderList(0))

    Request[Any](request.copy(headers = theHeaders), request.body)
  }

  def logout =
    Action { implicit request =>
      Ok(views.html.index(loginForm)).withNewSession
    }

  def pageB = deadbolt.SubjectPresent(new MyDeadboltHandler) {
    Action { implicit request =>
      Ok(views.html.pageB())
    }
  }

  import play.api.libs.json._

  val ajaxTextRds = (__ \ 'buttonID).read[Long]

  def ajaxText = deadbolt.SubjectPresent(new MyDeadboltHandler) {
    Action(parse.json) { request =>
      request.body.validate[Long](ajaxTextRds).map {
        case (buttonID) => {
          val text = "Pressed " + buttonID + " at " + new java.util.Date()
          Ok(Json.obj("status" -> "OK", "divID" -> buttonID, "text" -> text))
        }
      }.recoverTotal {
        e => BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(e)))
      }
    }
  }
}