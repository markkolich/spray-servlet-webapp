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

package com.kolich.spray.auth

import scala.concurrent.{ Promise, ExecutionContext, Future }

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

  // The name of the "session" cookie sent down to the client,
  // usually a browser.
  private val sessionCookieName = "SESSION"

  // Incoming cookie looks like SESSION="foobar" (with quotes)
  // We only want the "foobar" part, no quotes.
  private val sessionCookieRegex = """SESSION="([^"]+)"""".r

  def apply(cookie: HttpCookie): SessionCookie =
    sessionCookieRegex.findFirstMatchIn(cookie.value) match {
      // Cookie was found, return the value contained in the first
	  // capturing group.
      case Some(m) => apply(token = m.group(1))
      // Input string value did not match regex, no cookie or malformed
      // cookie header? Returns a new cookie with an empty "" token (will
      // never match any session).
      case _ => apply()
    }

  def getSessionCookieHeader(content: String = "",
    expires: Option[DateTime] = None,
    maxAge: Option[Long] = None): `Set-Cookie` =
      `Set-Cookie`(HttpCookie(name = sessionCookieName,
      	content = content,
      	expires = expires,
      	maxAge = maxAge,
      	// Set "secure = true" if you want the "Secure" flag set on
      	// outgoing cookies, meaning the cookie will only be accepted
      	// over HTTPS (secure) connections.
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