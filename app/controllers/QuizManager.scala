package controllers

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import akka.pattern.pipe
import common.AppProtocol._
import dao.{ThemesDao, SubjectsDao, QuestionsDao}

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
  * @author Bunyod (bunyodreal@gmail.com). Created at 2/11/16.
  */

object QuizManager {
  def props(questionsDao: QuestionsDao, subjectsDao: SubjectsDao, themesDao: ThemesDao) =
    Props(new QuizManager(questionsDao, subjectsDao, themesDao))
}

class QuizManager (questionsDao: QuestionsDao, subjectsDao: SubjectsDao, themesDao: ThemesDao) extends Actor with ActorLogging {

  log.info("Entry")

  implicit val executionContext = context.dispatcher
  implicit val defaultTimeout = Timeout(5.seconds)

  override def receive: Receive = {

    case CreateQuestions(questions) =>
      log.info("CreateQuestions")
      createQuestions(questions).pipeTo(sender())

    case GetQuestions =>
      getQuestions.pipeTo(sender())

    case AddSubject(name) =>
      createSubject(name).pipeTo(sender())

    case AddTheme(subjectId, name) =>
      addTheme(subjectId, name).pipeTo(sender())

    case CreateQuestion(question) =>
      createQuestion(question).pipeTo(sender())

    case GetSubjects =>
      getSubjects().pipeTo(sender())

    case GetThemes =>
      getThemes().pipeTo(sender())

    case _ =>
      log.info(s"Receive: None")

  }

  private def createQuestions(questions: List[Question]): Future[_] = {
    questions.map { quest =>
      questionsDao.create(quest)
    }
    Future.successful(())

  }

  private def createSubject(name: String): Future[Int] = {
    subjectsDao.create(Subject(name=name))
  }

  private def getSubjects() = {
    subjectsDao.getSubjects()
  }

  private def addTheme(subjectId: Int, name: String) = {
    themesDao.create(Theme(subjectId = Some(subjectId), name = Some(name)))
  }

  private def getThemes() = {
    themesDao.getThemes()
  }

  private def createQuestion(question: Question) = {
    questionsDao.create(question)
  }

  @tailrec
  private def replacer(str: String): String = {
    if (str.contains("%%")) {
      val rowRegex = """(?s)(%%.+?%%)""".r
      val repVal = rowRegex.findAllIn(str).matchData.map { m =>
        val a = m.toString()
        val imgName = a.substring(2, a.length-2)
        val b = s"<img src=/assets/quest_files/$imgName>"
        str.replaceAllLiterally(a, b)
      }
      replacer(repVal.next())
    } else {
      str
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

      Question(
        id = question.id,
        question = Some(rQuest),
        ansA = Some(rAnsA),
        ansB = Some(rAnsB),
        ansC = Some(rAnsC),
        ansD = Some(rAnsD),
        rAns = None,
        themeId = question.themeId,
        subjectId = question.subjectId
      )
    })
  }

}