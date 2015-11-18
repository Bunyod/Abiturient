package common

import java.util.Date

import ab.utils.EnumMappedToDb


/**
 * Created by bunyod on 11/17/15.
 */

object entities {

  object GenderType extends EnumMappedToDb {
    val Male = Value(0)
    val Female = Value(1)
  }

  case class User
  (
    id: Option[Int] = None,
    firstName: String,
    lastName: String,
    secondName: String,
    login: String,
    password: String,
    gender: GenderType.Value,
    bDay: Date
  )

  case class RegUser(user: User)
}
