package common

import java.util.Date

import ab.utils.EnumMappedToDb

/**
 *
 * @author Bunyod Bobojonov (bunyodreal@gmail.com). Created at 11/17/15.
 */

object entities {

  object GenderType extends EnumMappedToDb {
    val Male = Value(0)
    val Female = Value(1)
  }

  case class User
  (
    id: Option[Int] = None,
    firstName: Option[String],
    lastName: Option[String],
    secondName: Option[String],
    login: String,
    password: String,
    gender: Option[GenderType.Value],
    bDay: Option[Date]
  )

  case class SessionUser
  (
    login: String,
    password: String
    )

  case class RegUser(user: User)
}
