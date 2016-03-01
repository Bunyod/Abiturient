package ab.utils

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.commons.lang3.StringUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Json, _}

import scala.util.Try

object JsonFormatUtils {

  implicit def optDateTimeFormat(fieldName: String, dateFormat: String = "yyyy-MM-dd hh:mm aa") =
    (__ \ fieldName).read[String].map(s => Try(new SimpleDateFormat(dateFormat).parse(s)).toOption)

  def optStringRead(fieldName: String) =
    (__ \ fieldName).readNullable[String].map { s =>
      if (s.exists(StringUtils.isNotBlank)) s else None
    }

}
