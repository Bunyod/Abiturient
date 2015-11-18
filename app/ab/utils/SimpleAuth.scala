package ab.utils

import play.api.mvc.{Controller, RequestHeader, Result}

import scala.concurrent.Future

trait SimpleAuth { self: Controller =>

  def authBy(sessionAttr: String)(result: => Result)(implicit request: RequestHeader): Result = {
    request.session.get(sessionAttr).map { _ =>
      result
    }.getOrElse {
      Unauthorized
    }
  }

  def authByAsync(sessionAttr: String)(result: => Future[Result])(implicit request: RequestHeader): Future[Result] = {
    request.session.get(sessionAttr).map { _ =>
      result
    }.getOrElse {
      Future.successful(Unauthorized)
    }
  }

}
