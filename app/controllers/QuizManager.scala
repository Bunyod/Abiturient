package controllers

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import akka.pattern.pipe
import common.AppProtocol.{CreateQuestions, Question}
import dao.QuestionsDao

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
  * @author Bunyod (bunyodreal@gmail.com). Created at 2/11/16.
  */

object QuizManager {
  def props(questionsDao: QuestionsDao) =
    Props(new QuizManager(questionsDao))
}

class QuizManager (questionsDao: QuestionsDao) extends Actor with ActorLogging {

  log.info("Entry")

  implicit val executionContext = context.dispatcher
  implicit val defaultTimeout = Timeout(5.seconds)

  override def receive: Receive = {

    case CreateQuestions(questions) =>
      log.info("CreateQuestions")
      createQuestions(questions).pipeTo(sender())

    case _ =>
      log.info(s"Receive: None")

  }

  private def createQuestions(questions: List[Question]): Future[_] = {
    questions.map { quest =>
      questionsDao.create(quest)
    }
    Future.successful(())

  }

}