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

class FileReader extends Controller {

  def ind() = Action { implicit req =>
    val docFile = new File(Play.getFile("public/"), "savollar.docx")
    val docx = new XWPFDocument(OPCPackage.openOrCreate(docFile))
    val picData = docx.getAllPictures.get(0).getData
    val picName= docx.getAllPictures.get(0).getFileName
    val picType = docx.getAllPictures.get(0).getPictureType


    val paragraphs = docx.getParagraphs
    for (i <- 0 to paragraphs.size()-1) {
      val paragraph = paragraphs.get(i)
      val element = paragraph.getRuns
      for (i <- 0 to element.size() - 1) {
        val paragraph = element.get(i)
        val pics= paragraph.getEmbeddedPictures
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

    Logger.debug(s"TTTT = ${wx.getText}")

    Ok("asdf")
  }



}