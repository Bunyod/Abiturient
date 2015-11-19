package security

import javax.inject.Singleton

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import dao.UsersDao

/**
 * Created by bunyod on 11/19/15.
 */

@Singleton
class MyHandlerCache(usersDao: UsersDao) extends HandlerCache {

  val defaultHandler: DeadboltHandler = new MyDeadboltHandler

  val handlers: Map[Any, DeadboltHandler] = Map(HandlerKeys.defaultHandler -> defaultHandler,
                                                HandlerKeys.altHandler -> new MyDeadboltHandler(Some(MyAlternativeDynamicResourceHandler)),
                                                HandlerKeys.userlessHandler -> new MyUserlessDeadboltHandler
                                                )
  override def apply(): DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
