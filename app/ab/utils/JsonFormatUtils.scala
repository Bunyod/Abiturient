package ab.utils

import java.text.SimpleDateFormat

import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.util.Try

object JsonFormatUtils {

  implicit def optDateTimeFormat(fieldName: String, dateFormat: String = "yyyy-MM-dd hh:mm aa") =
    (__ \ fieldName).read[String].map(s => Try(new SimpleDateFormat(dateFormat).parse(s)).toOption)

  def optStringRead(fieldName: String) =
    (__ \ fieldName).readNullable[String].map { s =>
      if (s.exists(StringUtils.isNotBlank)) s else None
    }

}
