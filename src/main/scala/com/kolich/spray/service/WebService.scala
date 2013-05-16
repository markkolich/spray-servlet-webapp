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

import akka.actor._
import spray.routing._
import spray.util._
import spray.http._
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import MediaTypes._
import HttpMethods._

import com.kolich.spray.auth._
import com.kolich.spray.auth.cookie._
import com.kolich.spray.templating._
import com.kolich.spray.protocols._

trait WebService extends Service {
  
  override implicit val rejectionHandler: RejectionHandler = RejectionHandler.fromPF {
    case Nil => complete(NotFound, "Foobar! Your default 404 page handler here.")
    case MissingSessionCookieRejection() :: _ => complete(redirectToRoute("/login"))
    case WebAppAuthenticationRejection() :: _ => complete(redirectToRoute("/login"))
  }

}