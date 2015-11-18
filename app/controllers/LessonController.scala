package controllers

import play.api.mvc.{Action, Controller}

/**
 * Created by bunyod on 11/17/15.
 */
class LessonController extends Controller {

  def login() = Action {
    Ok(views.html.login())
  }

}
