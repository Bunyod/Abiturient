package controllers

/**
  * Created by bunyodreal@gmail.com on 2/9/16.
  */


import java.io.File
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
        val embPic = element.get(i)
        val pics= embPic.getEmbeddedPictures
        val ss= embPic.text()
        if (pics.size() > 0) {
          val picName = pics.get(0).getPictureData.getFileName
          val now = new Date()
          embPic.setText(s"%%${now.getTime}$picName%% ")
        }
      }


    }

    val wx = new XWPFWordExtractor(docx)

    val tt = wx.getText

    Logger.debug(s"TTTT = $tt")

    Ok("asdf")
  }



}