package com.kolich.spray.auth

import scala.concurrent.{Promise, ExecutionContext, Future}

import spray.routing._
import spray.routing.authentication._

import spray.util._
import spray.http._
import spray.http.HttpHeaders._

package object cookie {
  type CookieAuthenticator[T] = Option[SessionCookie] => Future[Option[T]]
}

case class SessionCookie(token: String = "") {
  lazy val value = token
  override def toString = value
}

object SessionCookie {

  private val sessionCookieName = "WEBAPP_SESSION"

  // Incoming cookie looks like ZEPHYR_SESSION="foobar" (with quotes) -- we only want the "foobar" part, no quotes
  private val sessionCookieRegex = """WEBAPP_SESSION="([^"]+)"""".r

  def apply(cookie: HttpCookie): SessionCookie =
    sessionCookieRegex.findFirstMatchIn(cookie.value) match {
      // Cookie was found, return the value contained in the first capturing group
      case Some(m) => apply(token = m.group(1))
      // Input string value did not match regex, no cookie or malformed cookie header?
      // Returns a new cookie with an empty "" token (will never match any session)
      case _ => apply()
  	}

  def getSessionCookieHeader(content: String = "", expires: Option[DateTime] = None, maxAge: Option[Long] = None):`Set-Cookie` =
    `Set-Cookie`(HttpCookie(name = sessionCookieName,
        content = content,
        expires = expires,
        maxAge = maxAge,
        secure = false,
        httpOnly = true))

  def getUnsetSessionCookieHeader = getSessionCookieHeader(maxAge = Some(0L))

}

class UserAuthenticator[T](val authenticator: cookie.CookieAuthenticator[T])
  (implicit val ec: ExecutionContext) extends WebAppAuthenticator[T] {
  
  override def authenticate(cookies: Option[Seq[HttpCookie]], ctx: RequestContext) = {
	authenticator {
      cookies match {
        // NOTE: Only returns the _first_ matched session cookie from the
        // request.  If the request contains more than one session cookie
        // (hack attack?), headOption ensures that only the first one is ever
        // processed and we totally ignore the others.
        case Some(cookie) => cookie.map(SessionCookie(_)).headOption
        case _ => None
      }
    }
  }

}