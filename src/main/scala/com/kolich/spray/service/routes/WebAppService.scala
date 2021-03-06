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

package com.kolich.spray.service.routes

import scala.language.postfixOps
import scala.concurrent.duration._

import com.kolich.spray._
import com.kolich.spray.service._
import com.kolich.spray.auth._
import com.kolich.spray.auth.SessionCookie._
import com.kolich.spray.auth.cookie._
import com.kolich.spray.templating._
import com.kolich.spray.models._
import com.kolich.spray.protocols._

import akka.actor._

import spray.routing._
import spray.util._
import spray.http._
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.http.MediaTypes._
import spray.http.HttpMethods._

class WebAppService extends Service {
    
  override implicit val rejectionHandler: RejectionHandler = RejectionHandler.fromPF {
    case Nil => renderError(NotFound, Map("publicResourcePath" -> getPublicResourcePath))
    case MissingSessionCookieRejection() :: _ => complete(redirectToRoute("/logout"))
    case WebAppAuthenticationRejection() :: _ => complete(redirectToRoute("/logout"))
  }
  
  override def receive = runRoute {
    path("") {
      (get | post | put | delete) {
        complete(redirectToRoute("/home"))
      }
    } ~
    path("home") {
      authenticate(authenticator()) { session =>
      	get {
      	  render("templates/home.ssp", Map("publicResourcePath" -> getPublicResourcePath, "session" -> session))
      	}
      }
    } ~
    path ("login") {
      get {
        parameters("error"?) { (error:Option[String]) =>
          render("templates/login.ssp", Map("publicResourcePath" -> getPublicResourcePath, "error" -> (error != None)))
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
        	    	logger.info("Created session (login): " + session)
        	    	sessionCache.setSession(session.id, session)
        	    	respondWithHeader(getSessionCookieHeader(content = session.id)) {
        	    		_.complete(redirectToRoute("/home"))
        	    	}
        	    }
        	    case _ => {
        	      respondWithHeader(getUnsetSessionCookieHeader) {
        	    	  _.complete(redirectToRoute("/login?error=1"))
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
                	  logger.info("Removed session (logout): " + session)
                  }
                  case _ => {
                	  // Ignore, silently.
                  }
                }
              }
              ctx.complete(redirectToRoute("/login"))
            }
          }
      }
    }
  } // runRoute

}