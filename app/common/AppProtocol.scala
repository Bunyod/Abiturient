package common

/**
 * Created by comp17 on 12/7/15.
 */
object AppProtocol {

  case class LoginUser(login: String, password: String)
  sealed trait UserAuthFailure
  case class GeneralAuthFailure(failReason: String) extends UserAuthFailure
  case object UserRoleFailure extends UserAuthFailure
}
