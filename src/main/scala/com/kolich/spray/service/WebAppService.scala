package com.kolich.spray.service

import scala.concurrent.duration._

import com.weiglewilczek.slf4s.{Logging, Logger}

import com.kolich.spray._
import com.kolich.spray.auth._
import com.kolich.spray.auth.SessionCookie._
import com.kolich.spray.auth.cookie._
import com.kolich.spray.templating._
import com.kolich.spray.models._

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
      	  render("templates/home.ssp", Map("session" -> session))
      	}
      }
    } ~
    path ("login") {
      get {
        parameters("error"?) { (error:Option[String]) =>
        	render("templates/login.ssp", Map("error" -> (error != None)))
        }
      } ~
      post {
        formFields("username"?, "password"?) {
          (username: Option[String], password: Option[String]) => {
        	  val session:Option[SessionData] = {
        	    if(password.getOrElse("") == "foobar") {
        	      Some(SessionData(id = sessionCache.getRandomSessionId,
        	          username = username.getOrElse("")))
        	    } else {
        	      None
        	    }
        	  }
        	  session match {
        	    case Some(session) => {
        	    	sessionCache.setSession(session.id, session)
        	    	respondWithHeader(getSessionCookieHeader(content = session.id)) {
        	    		_.redirect(ApplicationConfig.rootPath + "/home")
        	    	}
        	    }
        	    case _ => {
        	      respondWithHeader(getUnsetSessionCookieHeader) {
        	    	  _.redirect(ApplicationConfig.rootPath + "/login?error=1")
        	      }
        	    }
        	  }
          }
        }
      }
    } ~
    path("logout") {
      (get | post | put | delete) {
          // Clobber session, then redirect to login page.  Sets a cookie
    	  // with an empty value ("") and sets its max-age to zero effectively
    	  // telling the client (usually a browser) to delete it.
          respondWithHeader(getUnsetSessionCookieHeader) { ctx => {
              for {
                `Cookie`(cookies) <- ctx.request.headers
                sessionCookies = cookies.map(SessionCookie(_))
                cookie <- sessionCookies
                sessionId = cookie.value
              } {
                // Remove the session from the internal session cache.
                sessionCache.removeSession(sessionId) match {
                  case Some(session:SessionData) => {
                	  logger.info("Removed web-session (logout) from session cache: " + session.id)
                  }
                  case _ => {
                	  // Ignore, silently.
                  }
                }
              }
              ctx.redirect(ApplicationConfig.rootPath + "/login")
            }
          }
      }
    }
  }

}