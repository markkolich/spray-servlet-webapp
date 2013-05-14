/**
 * Copyright (c) 2013 Mark S. Kolich
 * http://mark.koli.ch
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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
  
  // Needs to be here because of the implicit ExecutionContext
  // provided by Akka in this context.
  private def webAppAuth[U](authenticator: CookieAuthenticator[U] = SessionCookieAuthenticator()) =
    new UserAuthenticator[U](authenticator)
    
  private def getRedirectToRoute(route: String): HttpResponse = {
    HttpResponse(status = Found, headers = List(Location(ApplicationConfig.rootPath + route)))    
  }
  
  implicit val webAppServiceRejectionHandler = RejectionHandler.fromPF {
    case MissingSessionCookieRejection() :: _ => complete(getRedirectToRoute("/login"))
    case WebAppAuthenticationRejection() :: _ => complete(getRedirectToRoute("/login"))
  }
  
  override def receive = runRoute {    
    path("") {
      (get | post | put | delete) {
        complete(getRedirectToRoute("/home"))
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
        	    	logger.info("Created session (login), session ID: " + session)
        	    	sessionCache.setSession(session.id, session)
        	    	respondWithHeader(getSessionCookieHeader(content = session.id)) {
        	    		_.complete(getRedirectToRoute("/home"))
        	    	}
        	    }
        	    case _ => {
        	      respondWithHeader(getUnsetSessionCookieHeader) {
        	    	  _.complete(getRedirectToRoute("/login?error=1"))
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
    	  // telling the client (usually a browser) to delete the session cookie.
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
                	  logger.info("Removed session (logout) from " +
                	      "session cache: " + session)
                  }
                  case _ => {
                	  // Ignore, silently.
                  }
                }
              }
              ctx.complete(getRedirectToRoute("/login"))
            }
          }
      }
    }    
  } // runRoute

}