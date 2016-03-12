package controllers

/**
  * Created by bunyodreal@gmail.com on 2/9/16.
  */


import java.io.{FileOutputStream, File}
import java.util.Date
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import common.AppProtocol.{CreateQuestions, Question}
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import play.api.{Logger, Play}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt


class FileReader @Inject() (val actorSystem: ActorSystem)
                           (implicit ec: ExecutionContext) extends Controller {

  implicit val defaultTimeout = Timeout(5.seconds)
  val config = current.configuration.getConfig("web-server").get
  val quizManger = actorSystem.actorSelection(config.getString("quiz-manager-actor-path").get)

  def ind() = Action.async { implicit req =>

    val docFile = new File(Play.getFile("public/"), "savollar.docx")
    val docx = new XWPFDocument(OPCPackage.openOrCreate(docFile))
    val picData = docx.getAllPictures.get(0).getData
    val picName = docx.getAllPictures.get(0).getFileName
    val picType = docx.getAllPictures.get(0).getPictureType


    val paragraphs = docx.getParagraphs
    for (i <- 0 to paragraphs.size() - 1) {
      val paragraph = paragraphs.get(i)
      val element = paragraph.getRuns
      for (i <- 0 to element.size() - 1) {
        val paragraph = element.get(i)
        val pics = paragraph.getEmbeddedPictures
        if (pics.size() > 0) {
          val picName = pics.get(0).getPictureData.getFileName
          val now = new Date()
          val genName = s"${now.getTime}$picName"
          val placeholder = s"%%$genName%%"
          val out = new FileOutputStream(new File(Play.getFile("public/quest_files/"), genName))
          out.write(pics.get(0).getPictureData.getData)
          paragraph.setText(s"$placeholder ")
        }
      }

    }

    val wx = new XWPFWordExtractor(docx)

    val pattern = """[^##.](.*)[$\n##.]""".r

    val m = pattern.findAllIn(wx.getText).toList

    val quest = Question(None,None,None,None,None,None, None,None,None)
    val quests = List[Question](quest)

    val result = recQuest(m, quests).filter(_.question.isDefined).map { q =>

      if (q.ansA.get.contains("A)*")) {
          q.copy(
            ansA = q.ansA.map(_.substring(3)),
            ansB = q.ansB.map(_.substring(2)),
            ansC = q.ansC.map(_.substring(2)),
            ansD = q.ansD.map(_.substring(2)),
            rAns = Some("A")
          )
      } else if(q.ansB.get.contains("B)*")) {
          q.copy(
            ansA = q.ansA.map(_.substring(2)),
            ansB = q.ansB.map(_.substring(3)),
            ansC = q.ansC.map(_.substring(2)),
            ansD = q.ansD.map(_.substring(2)),
            rAns = Some("B")
          )
      } else if(q.ansC.get.contains("C)*")) {
          q.copy(
            ansA = q.ansA.map(_.substring(2)),
            ansB = q.ansB.map(_.substring(2)),
            ansC = q.ansC.map(_.substring(3)),
            ansD = q.ansD.map(_.substring(2)),
            rAns = Some("C")
          )
      } else if(q.ansD.get.contains("D)*")) {
          q.copy(
            ansA = q.ansA.map(_.substring(2)),
            ansB = q.ansB.map(_.substring(2)),
            ansC = q.ansC.map(_.substring(2)),
            ansD = q.ansD.map(_.substring(3)),
            rAns = Some("D")
          )
      } else {
        q
      }
    }

    (quizManger ? CreateQuestions(result)).map { _ =>
      Ok("asdf")
    }

  }

  @tailrec
  private def recQuest(lst: List[String], quests: List[Question]): List[Question] = {
    lst match {
      case Nil => quests
      case h :: tail =>

        if (tail.isDefinedAt(3) && tail(3).contains("D)")) {

          val question = quests.last
          val quest = question.copy(
            question = Some(question.question.getOrElse("") + h.replaceAll("\r\n", "")),
            ansA = Some(tail(0).replaceAll("\n", "")),
            ansB = Some(tail(1).replaceAll("\n", "")),
            ansC = Some(tail(2).replaceAll("\n", "")),
            ansD = Some(tail(3).replaceAll("\n", ""))
          )

          val withEmpty =  List(quest) ::: List(Question(None,None,None,None,None,None,None,None,None))
          recQuest(tail.takeRight(tail.size - 4), quests.take(quests.size - 1) ::: withEmpty)
        } else {
          val quest = quests.last
          val q = quest.copy(
            question = Some(quest.question.getOrElse("") + h.replaceAll("\r\n", ""))
          )

          recQuest(tail, quests.take(quests.size - 1) ::: List(q))
        }
    }
  }

}