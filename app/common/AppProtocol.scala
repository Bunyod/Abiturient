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

  implicit val questionsFormat = Json.format[Question]

  case class CreateQuestions(questions: List[Question])
  case object GetQuestions

}
