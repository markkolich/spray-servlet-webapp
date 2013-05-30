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

class AjaxApiService extends Service {
  
  import WebAppJsonFormat._ // Brings the JSON formatters into scope.
  
  override implicit val rejectionHandler: RejectionHandler = RejectionHandler.fromPF {
    case Nil => complete(NotFound,
        Error(errorCode = NotFound.value, message = Some(NotFound.defaultMessage)))
    case MissingSessionCookieRejection() :: _ => complete(Unauthorized,
        Error(errorCode = Unauthorized.value, message = Some(Unauthorized.defaultMessage)))
    case WebAppAuthenticationRejection() :: _ => complete(Unauthorized,
        Error(errorCode = Unauthorized.value, message = Some(Unauthorized.defaultMessage)))
  }
  
  override def receive = runRoute {
	  path("users") {
		  authenticate(authenticator()) { session =>
	  		get {
	  		  // You'd almost never have static data like this embedded in
	  		  // your code, but this is an example, right?  In the real world
	  		  // you'd probably load this data from a database or call a
	  		  // external web-service.
	  		  val users = List(
	  		      User("Lisa", "lisa@simpsons.com", "555-111-1224"),
	  		      // Note the stray <script> on Bart.  This will be rendered
	  		      // out safely (with nasty characters properly encoded to prevent
	  		      // XSS attacks in dynamic data). This demonstrates the "HTML
	  		      // safe JSON" support injected into spray-json.
	  		      User("Bart", "bart@simpsons.com", "555-222-1234",
	  		          Some("""<script>alert("eat my shorts")</script>""")), // He He
	  		      User("Homer", "homer@simpsons.com", "555-222-1244"),
	  		      User("Marge", "marge@simpsons.com", "555-222-1254"),
	  		      User("Maggie", "maggie@simpsons.com", "555-333-8782")
	  		  )
	  		  _.complete(OK, ListResponse(users.size, true, users))
	  		} ~
	  		post {
	  		  entity(as[User]) { user =>
	  		    // Insert user into database or some other store here.
	  		    // Send the raw user, as received, back to the caller
	  		    // (for obvious demo purposes).
	  		    _.complete(user)
	  		  }
	  		}
	  	}
	  }
  }

}
