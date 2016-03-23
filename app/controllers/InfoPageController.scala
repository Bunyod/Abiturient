package controllers

import play.api.mvc.Action

/**
  * @author Bunyod (bunyodreal@gmail.com). Created at 12/03/16.
  */
trait InfoPageController { self: Application =>

  def about = Action { implicit request =>
    Ok(views.html.about())


  }

  def services = Action { implicit request =>
    Ok(views.html.services())

  }

  def portfolio = Action { implicit request =>
    Ok(views.html.portfolio())
  }

  def career = Action { implicit request =>
    Ok(views.html.career())
  }

  def blogItem = Action { implicit request =>
    Ok(views.html.blogItem())
  }

  def faq = Action { implicit request =>
    Ok(views.html.faq())
  }

  def privacy = Action { implicit request =>
    Ok(views.html.privacy())
  }

  def blog = Action { implicit request =>
    Ok(views.html.blog())
  }

  def contactUs = Action { implicit request =>
    Ok(views.html.contactUs())
  }

  def terms = Action { implicit request =>
    Ok(views.html.terms())
  }


  def registration = Action { implicit request =>
    Ok(views.html.registration())
  }

  def results = Action { implicit request =>
    Ok(views.html.results())
  }

  def addQuestion = Action { implicit  request =>
    Ok(views.html.admin.question())
  }

  def authFailed = Action { implicit  request =>
    Ok(views.html.failed())
  }


}
