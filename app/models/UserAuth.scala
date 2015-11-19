package models

import be.objectify.deadbolt.core.models.Subject
import play.libs.Scala

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

case class UserAuth(userName: String) extends Subject {
  def getRoles: java.util.List[SecurityRole] = {
    Scala.asJava(List(SecurityRole("ADMIN"),
                      SecurityRole("USER"),
                      SecurityRole("ADMINISTATOR")))
  }

  def getPermissions: java.util.List[SecurityPermission] = {
    Scala.asJava(List[SecurityPermission]())
  }

  def getIdentifier: String = userName
}

object User {
  import play.api.Play
  private lazy val adminUsername = Play.current.configuration.getString("login.username").getOrElse("")
  private lazy val adminPassword = Play.current.configuration.getString("login.password").getOrElse("")

  def authenticate(username: String, password: String): Option[UserAuth] =
    if ((adminUsername == username) && (adminPassword == password))
      Some(UserAuth(username))
    else
      None
}