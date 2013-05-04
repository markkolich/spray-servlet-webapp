package com.kolich.spray.auth

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.kolich.spray.auth.cookie.CookieAuthenticator
import com.kolich.spray.models.SessionData

trait Secure {
  
  protected val sessionCache = SessionCache

  object SessionCookieAuthenticator {
    def apply()(implicit ec: ExecutionContext): CookieAuthenticator[SessionData] = {
      new CookieAuthenticator[SessionData] {
        def apply(cookie: Option[SessionCookie]) = Future.successful(
          // Return None for a missing, or invalid, session. Triggers a failure
          // with the authenticate() directive meaning this user/session couldn't
          // be validated. The response either redirects the user to the "login"
          // page or in the case of UI API type responses, returns a proper JSON
          // object indicating failure.
          cookie match {
            case Some(cookie) => sessionCache.getSession(cookie.value)
            case _ => None
          }
        )
      }
    }
  }

}