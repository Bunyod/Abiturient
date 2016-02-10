package controllers

/**
  * Created by bunyodreal@gmail.com on 2/9/16.
  */


import java.io.{FileOutputStream, File}
import java.util.Date

import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import play.api.{Logger, Play}

import scala.annotation.tailrec

class FileReader extends Controller {
  case class Question
  (
    question: Option[String],
    ansA: Option[String],
    ansB: Option[String],
    ansC: Option[String],
    ansD: Option[String]
  )

  def ind() = Action { implicit req =>

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
          //          out.write(pics.get(0).getPictureData.getData)
          paragraph.setText(s"$placeholder ")
        }
      }

    }

    val wx = new XWPFWordExtractor(docx)

    val pattern = """[^##.](.*)[$\n##.]""".r

    val m = pattern.findAllIn(wx.getText).toList

    val quest = Question(None,None,None,None,None)
    val quests = List[Question](quest)

    val res = recQuest(m, quests)
    Logger.debug(s"RESSS = $res")

    Ok("asdf")
  }

  @tailrec
  private def recQuest(lst: List[String], quests: List[Question]): List[Question] = {
    lst match {
      case Nil => quests
      case h :: tail =>

        if (tail.isDefinedAt(3) && tail(3).contains("D)")) {

          val quest = quests.last
          val q = quest.copy(
            question = Some(quest.question.getOrElse("") + h),
            ansA = Some(tail(0)),
            ansB = Some(tail(1)),
            ansC = Some(tail(2)),
            ansD = Some(tail(3))
          )
          recQuest(tail.takeRight(tail.size - 5), quests ::: List(q))
        } else {
          val quest = quests.last
          val q = quests.last.copy(
            question = Some(quests.last.question.getOrElse("") + h)
          )
          //TODO substitute this element with last element of the quests list
          recQuest(tail.takeRight(tail.size - 1), quests)
        }
    }
  }

}