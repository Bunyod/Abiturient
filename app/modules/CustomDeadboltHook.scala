package modules

import be.objectify.deadbolt.scala.cache.HandlerCache
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import security.MyHandlerCache

/**
 * Created by comp17 on 11/19/15.
 */
class CustomDeadboltHook extends Module{
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
  Seq(bind[HandlerCache].to[MyHandlerCache])

}
