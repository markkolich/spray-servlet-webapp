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
  
  import WebAppJsonFormat._ // Important; needed to bring the JSON formatters into scope.
  
  override implicit val rejectionHandler: RejectionHandler = RejectionHandler.fromPF {
    case Nil => complete(NotFound, "Foobar! Your ajax 404 page handler here.")
    case MissingSessionCookieRejection() :: _ => complete(NotFound, "missing cookie")
    case WebAppAuthenticationRejection() :: _ => complete(NotFound, "bad cookie")
  }
  
  override def receive = runRoute {
	  path("") {
		  logger.warn("-----here")
		  authenticate(authenticator()) { session =>
	  		get {
	  			_.complete("sup dude")
	  		}
	  	}
	  }
  }

}