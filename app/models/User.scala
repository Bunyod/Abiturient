package models
import be.objectify.deadbolt.core.models.Subject
import play.libs.Scala

/**
 *
 * @author Bunyod Bobojonov (bunyodreal@gmail.com). Created at 11/17/15.
 */

case class User(userName: String, roles: List[SecurityRole]) extends Subject {

  def getRoles: java.util.List[SecurityRole] = Scala.asJava(roles)

  def getPermissions: java.util.List[SecurityPermission] = {
    Scala.asJava(List[SecurityPermission]())
  }

  def getIdentifier: String = userName

}