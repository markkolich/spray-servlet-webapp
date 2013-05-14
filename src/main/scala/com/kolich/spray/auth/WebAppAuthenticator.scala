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

case class MissingSessionCookieRejection() extends Rejection
case class WebAppAuthenticationRejection() extends Rejection

trait WebAppAuthenticator[T] extends ContextAuthenticator[T] {

  def apply(ctx: RequestContext) = {
    val cookieHeaders = ctx.request.headers.findByType[`Cookie`]
    val cookies = cookieHeaders.map { case Cookie(cookie) => cookie }
    authenticate(cookies, ctx) map {
      case Some(sessionData) => Right(sessionData)
      case _ => Left {
        // No cookies were provided.
        if (cookieHeaders.isEmpty) MissingSessionCookieRejection()
        // Cookies were provided but validation of those session cookies failed.
        else WebAppAuthenticationRejection()
      }
    }
  }

  implicit def ec: ExecutionContext

  def authenticate(cookies: Option[Seq[HttpCookie]], ctx: RequestContext): Future[Option[T]]

}