package models

import be.objectify.deadbolt.core.models.Role

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

case class SecurityRole(roleName: String) extends Role {
  override def getName: String = roleName
}