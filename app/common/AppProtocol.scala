package common

import java.util.Date

import ab.utils.EnumMappedToDb
import play.api.libs.json.Json

/**
 *
 * @author Bunyod Bobojonov (bunyodreal@gmail.com). Created at 11/17/15.
 */

object AppProtocol {

  object GenderType extends EnumMappedToDb {
    val Male = Value(0)
    val Female = Value(1)
  }

  object UserRoleType extends EnumMappedToDb {
    val User = Value(0)
    val Admin = Value(1)
  }

  case class AbUser
  (
    id: Option[Int] = None,
    firstName: Option[String],
    lastName: Option[String],
    secondName: Option[String],
    login: String,
    password: String,
    gender: Option[GenderType.Value],
    bDay: Option[Date],
    roles: String
  )

  case class SessionUser
  (
    login: String,
    password: String
  )

  case class RegUser(user: AbUser)

  case class LoginUser(login: String, password: String)
  sealed trait UserAuthFailure
  case class GeneralAuthFailure(failReason: String) extends UserAuthFailure
  case object UserRoleFailure extends UserAuthFailure
  case class Question
  (
    id: Option[Int] = None,
    subjectId: Option[Int] = None,
    themeId: Option[Int] = None,
    question: Option[String],
    ansA: Option[String],
    ansB: Option[String],
    ansC: Option[String],
    ansD: Option[String],
    rAns: Option[String]
  )

  case class Theme
  (
    id: Option[Int] = None,
    subjectId: Option[Int],
    name: Option[String]
  )

  case class Subject
  (
    id: Option[Int] = None,
    name: String
  )

  implicit val questionsFormat = Json.format[Question]

  case class CreateQuestions(questions: List[Question])
  case object GetQuestions

  case class AddSubject(name: String)
  case class AddTheme(subjectId: Int, name: String)
  case class CreateQuestion(question: Question)
  case object GetSubjects
  case object GetThemes

  implicit val subjectsFormat = Json.format[Subject]
  implicit val addThemeFormat = Json.format[AddTheme]
  implicit val themesFormat = Json.format[Theme]

  case class GetQuestionsByParams(subjectId: Option[Int], themeId: Option[Int], level: Option[Int], limit: Option[Int])
}
