package common

import play.api.libs.json.Json

/**
 *
 * @author Bunyod Bobojonov (bunyodreal@gmail.com). Created at 11/17/15.
 */

object AppProtocol {

  case class LoginUser(login: String, password: String)
  sealed trait UserAuthFailure
  case class GeneralAuthFailure(failReason: String) extends UserAuthFailure
  case object UserRoleFailure extends UserAuthFailure
  case class Question
  (
    id: Option[Int] = None,
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
  case object GetSubjects

  implicit val subjectsFormat = Json.format[Subject]

}
