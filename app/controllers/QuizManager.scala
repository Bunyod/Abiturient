package controllers

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import akka.pattern.pipe
import common.AppProtocol.{GetQuestions, CreateQuestions, Question}
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

    case GetQuestions =>
      getQuestions.pipeTo(sender())

    case _ =>
      log.info(s"Receive: None")

  }

  private def createQuestions(questions: List[Question]): Future[_] = {
    questions.map { quest =>
      questionsDao.create(quest)
    }
    Future.successful(())

  }

  def replacer(repr: String) = {
    val rowRegex = "(?s)(%%.+?%%)".r
    val repVal = rowRegex.findAllIn(repr).matchData map { m =>
      val a = m.toString()
      val imgName = a.substring(2, a.length-2)
      val imgPath = s"/public/quest_files"
      log.info(s"imgPath=$imgPath")
      val b = s"<img src=@routes.Assets.at(&quot;/public/images/quest_files/&quot;&#44;&quot;$imgName&quot;)>"
      repr.replaceAllLiterally(a, b)
    }

    if (repVal.hasNext) {
      repVal.next()
    } else {
      repr
    }

  }


  private def getQuestions() = {
    questionsDao.getQuestions().map(_.map { question =>
      val quest = question.question
      val ansA = question.ansA
      val ansB = question.ansB
      val ansC = question.ansC
      val ansD = question.ansD

      val rQuest = replacer(quest.get)
      val rAnsA = replacer(ansA.get)
      val rAnsB = replacer(ansB.get)
      val rAnsC = replacer(ansC.get)
      val rAnsD = replacer(ansD.get)

      question.copy(
        question = Some(rQuest.toString()),
        ansA = Some(rAnsA.toString()),
        ansB = Some(rAnsB.toString()),
        ansC = Some(rAnsC.toString()),
        ansD = Some(rAnsD.toString())
      )
    })
  }

}