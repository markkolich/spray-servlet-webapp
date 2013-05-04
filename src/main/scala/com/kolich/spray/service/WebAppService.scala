package com.kolich.spray.service

import scala.concurrent.duration._

import com.weiglewilczek.slf4s.{Logging, Logger}

import com.kolich.spray._
import com.kolich.spray.auth._
import com.kolich.spray.auth.cookie._
import com.kolich.spray.templating._

import akka.actor._
import spray.util._
import spray.http._
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.routing._
import MediaTypes._
import HttpMethods._

class WebAppService extends Actor with HttpServiceActor with Secure with ScalateSupport with Logging {
  
  implicit val webAppServiceRejectionHandler = RejectionHandler.fromPF {
    case MissingSessionCookieRejection() :: _ =>
      complete(HttpResponse(status = Found, headers = List(Location(ApplicationConfig.rootPath + "/login"))))
    case WebAppAuthenticationRejection() :: _ =>
      complete(HttpResponse(status = Found, headers = List(Location(ApplicationConfig.rootPath + "/login"))))
  }
  
  // Needs to be here because of the implicit ExecutionContext provided by Akka in this context.
  private def webAppAuth[U](authenticator: CookieAuthenticator[U] = SessionCookieAuthenticator()) =
    new UserAuthenticator[U](authenticator)
  
  override def receive = runRoute {
    path("") {
      (get | post | put | delete) {
        complete(HttpResponse(status = Found, headers = List(Location(ApplicationConfig.rootPath + "/home"))))
      }
    } ~
    path("home") {
      authenticate(webAppAuth()) { session =>
      	get {
      	  render("templates/home.ssp")
      	}
      }
    } ~
    path ("login") {
      get {
        render("templates/login.ssp")
      } ~
      post {
        complete("Meh")
      }
    }
  }

}