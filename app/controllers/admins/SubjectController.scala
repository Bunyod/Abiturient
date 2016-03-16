package controllers.admins

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.AppProtocol._
import controllers.FileReader
import dao.UsersDao
import play.api.Play._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration.DurationInt

/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 20/02/16.
 */

class SubjectController @Inject()(val actorSystem: ActorSystem,
                                  deadbolt: DeadboltActions,
                                  usersDao: UsersDao,
                                  actionBuilders: ActionBuilders)
                                 (implicit ec: ExecutionContext) extends Controller with LazyLogging {

  implicit val defaultTimeout = Timeout(5.seconds)
  val config = current.configuration.getConfig("web-server").get
  val quizManager = actorSystem.actorSelection(config.getString("quiz-manager-actor-path").get)


  def showAddQuiz() = deadbolt.Restrict(List(Array("ADMIN"))) {
    Action { implicit request =>
      Ok(views.html.admin.addSubject())
    }
  }

  def subjects() =  Action.async { implicit request =>
    (quizManager ? GetSubjects).mapTo[Seq[Subject]].map { subjects =>
      Ok(Json.toJson(subjects))
    }
  }

  def themes() =  Action.async { implicit request =>
    (quizManager ? GetThemes).mapTo[Seq[Theme]].map { themes =>
      Ok(Json.toJson(themes))
    }
  }

  def addSubject() = deadbolt.Restrict(List(Array("ADMIN"))) {
    Action.async(parse.json[String]) { implicit request =>
      val subjectName = request.body
      (quizManager ? AddSubject(subjectName)).mapTo[Int].map { _ =>
        Ok(Json.toJson("Successfully added"))
      }
  }}

  def addTheme() = deadbolt.Restrict(List(Array("ADMIN"))) {
    Action.async(parse.json[AddTheme]) { implicit request =>
      val theme = request.body
      logger.info(s"theme=$theme")
      (quizManager ? theme).mapTo[Int].map { _ =>
        Ok(Json.toJson("Successfully added"))
      }
  }}

  def addQuestion() = deadbolt.Restrict(List(Array("ADMIN"))) {
    Action.async(parse.json[Question]) { implicit request =>
      val question = request.body
      logger.info(s"question=$question")
      (quizManager ? CreateQuestion(question)).mapTo[Int].map { _ =>
        Ok(Json.toJson("Successfully added"))
      }
  }}

  def getQuestions() = Action.async { implicit request =>

    (quizManager ? GetQuestions).mapTo[Seq[Question]].map { questions =>
      Ok(Json.toJson(questions))
    }
  }

  def addQuestionByFile() = Action.async(parse.multipartFormData) { implicit request =>
    val form = request.body.asFormUrlEncoded
    val questPart = form("dataForServer")(0)
    val json = Json.parse(questPart)
    val quest = json.validate[Question]
    val subjectId = quest.get.subjectId
    val themeId = quest.get.themeId


    request.body.file("question").map { contentFile =>
      val bytes = contentFile.ref.file
      val parsedQuestions = FileReader.parseFile(bytes, subjectId, themeId)
      (quizManager ? CreateQuestions(parsedQuestions)).map { _ =>
        Ok(Json.toJson("File has been successfully upload"))
      }
    }.getOrElse {
      Future.successful(Ok("Failed"))
    }
  }
}
