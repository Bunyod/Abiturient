package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import common.AppProtocol.{GeneralAuthFailure, LoginUser, UserAuthFailure}
import common.entities.SessionUser
import dao.{QuestionsDao, UsersDao}
import play.api.Play.current
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.libs.mailer.MailerClient
import play.api.libs.ws.WSClient
import play.api.mvc._
import security.MyDeadboltHandler

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 11/17/15.
 */


@Singleton
class Application @Inject() (val messagesApi: MessagesApi,
                             mailer: MailerClient,
                             ws: WSClient,
                             actorSystem: ActorSystem,
                             usersDao: UsersDao,
                             questionsDao: QuestionsDao,
                             deadbolt: DeadboltActions,
                             actionBuilder: ActionBuilders)
                           (implicit ec: ExecutionContext) extends Controller {

  case class LoginForm
  (
    login: String = "",
    password: String = ""
    )

  implicit val defaultTimeout = Timeout(5.seconds)
  //  val config = current.configuration.getConfig("web-server").get
  val userManager = actorSystem.actorOf(UserManager.props(usersDao), "user-manager")
  val quizManager = actorSystem.actorOf(QuizManager.props(questionsDao), "quiz-manager")

  val logger = Logger(this.getClass())

  logger.info("In AppController")
  implicit val sessionUserFormat = Json.format[SessionUser]

  val loginPlayForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> text) (LoginForm.apply)(LoginForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def loginPost = Action.async { implicit request =>
      loginPlayForm.bindFromRequest.fold(
        errorForm => { // binding failure
          Logger.error("Login failed for user " + errorForm.data("username"))
  //        Future.successful(Redirect(routes.Application.pageB()))
          Future.successful(Redirect(routes.Application.index()).flashing("error" -> "loginFailed"))
        },
        {
          case LoginForm(username, password) =>
            (userManager ? LoginUser(username, password)).mapTo[Either[UserAuthFailure, String]]
            .map {
              case Right(user)=>
                val modifiedRequest = updateRequestSession(request, List(("user" -> user)))
                Ok(views.html.pageA(modifiedRequest)).withSession(request.session + ("ab-user", username))
              case Left(GeneralAuthFailure(_)) =>
                Redirect(routes.Application.pageC())
            }
          case _ =>
            Future.successful(Redirect(routes.Application.index()).flashing("error" -> "loginFailed"))
        }
      )
  }



  private def updateRequestSession(request: Request[Any], additionalSessionParams: List[(String, String)]): Request[Any] = {
    import scala.language.reflectiveCalls

    val updatedSession = additionalSessionParams.foldLeft[Session](request.session) { _ + _ }
    val existingSession = request.headers.get(SET_COOKIE).map(cookies => Session.decodeFromCookie(Cookies.decodeCookieHeader(cookies).find(_.name == Session.COOKIE_NAME)))
    val newSession = if (existingSession.isDefined)
      existingSession.get.data.foldLeft(updatedSession) { _ + _ }
    else
      updatedSession

//    val cookies = Cookies.decodeCookieHeader(request.headers.get(COOKIE).get)
    val myCookies = Seq(Session.COOKIE_NAME -> Session.encodeAsCookie(newSession))
    val headerMap = request.headers.toMap +
      (COOKIE -> Seq(Cookies.encodeSetCookieHeader(myCookies.map(_._2))))
    var tmpHeaderList: ListBuffer[Tuple2[String, String]] = new ListBuffer[(String, String)]()
    for((k,v) <- headerMap){
      tmpHeaderList += new Tuple2(k, v.head)
    }
    val theHeaders = Headers(tmpHeaderList(0))

    Request[Any](request.copy(headers = theHeaders), request.body)
  }

  def logout = Action { implicit request =>
    Ok(views.html.index()).withNewSession
  }

  def pageB = deadbolt.SubjectPresent(new MyDeadboltHandler(None, usersDao)) {
    Action { implicit request =>
      Ok(views.html.pageB())
    }
  }

  def pageC = deadbolt.Restrict(List(Array("ADMIN"))) {
    Action { implicit request =>
      Ok(views.html.pageC())
    }
  }

  def pageD = deadbolt.Restrict(List(Array("USER"))) {
    Action { implicit request =>
      Ok(views.html.pageD())
    }
  }

}