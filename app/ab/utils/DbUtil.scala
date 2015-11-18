package ab.utils

import java.util.Date

import slick.driver.PostgresDriver.api._

trait EnumMappedToDb extends Enumeration {
  implicit def enumMapper = MappedColumnType.base[Value, Int](_.id, this.apply)
}

trait Date2SqlDate {
  implicit val date2SqlDate = MappedColumnType.base[Date, java.sql.Timestamp](
    d => new java.sql.Timestamp(d.getTime),
    d => new java.util.Date(d.getTime)
  )
}
