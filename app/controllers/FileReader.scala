package controllers

/**
  * Created by bunyodreal@gmail.com on 2/9/16.
  */


import java.io.File

import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import play.api.{Logger, Play}

class FileReader extends Controller {

  def ind() = Action { implicit req =>
    val docFile = new File(Play.getFile("public/"), "savol.docx")
    val docx = new XWPFDocument(OPCPackage.openOrCreate(docFile))
    val wx = new XWPFWordExtractor(docx)
    val text = wx.getText()
    Logger.debug(s"TT = $text")

    Ok("asdf")
  }



}