package controllers

import javax.inject.Inject

import akka.pattern.ask
import akka.actor.ActorSystem
import akka.util.Timeout
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import com.typesafe.scalalogging.slf4j.LazyLogging
import common.AppProtocol._
import play.api.Play._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt


/**
 * @author Bunyod (bunyodreal@gmail.com). Created at 20/02/16.
 */

class QuizController @Inject() (val actorSystem: ActorSystem,
                                deadbolt: DeadboltActions,
                                actionBuilders: ActionBuilders)
                               (implicit ec: ExecutionContext) extends Controller with LazyLogging {

  implicit val defaultTimeout = Timeout(5.seconds)
  val config = current.configuration.getConfig("web-server").get
  val quizManger = actorSystem.actorSelection(config.getString("quiz-manager-actor-path").get)


  def getQuestions() = Action.async { implicit request =>

    (quizManger ? GetQuestions).mapTo[Seq[Question]].map { questions =>
      Ok(Json.toJson(questions))
    }

  }
}