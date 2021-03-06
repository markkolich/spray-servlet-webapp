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

package com.kolich.spray

import com.weiglewilczek.slf4s.Logging

import com.kolich.spray.service._
import com.kolich.spray.service.routes._

import akka.actor._
import akka.routing._

import spray.servlet._
import spray.routing._

class Boot extends WebBoot with Logging {

  val system = ActorSystem("spray-servlet-webapp")
    
  val ajaxApiService = system.actorOf(Props[AjaxApiService])
  val webAppService = system.actorOf(Props[WebAppService])
  
  class RootServiceActor extends Actor with HttpServiceActor {
    override def receive = runRoute {
      pathPrefix("api") {
    	  // Any requests that start with "api" are forwarded to the AJAX
    	  // API service actor.  Note that starts with "api" does not
    	  // include the Spray "root-path".  For example, for a raw HTTP
    	  // request of GET:/app/api/foobar.json the starts with "api" only
    	  // applies to to the "/api/foobar.json" piece of the URI.
    	  ajaxApiService ! _
      } ~ {
    	  // Any other request that does not start with "api" is
    	  // sent here, the generic web-app service.
    	  webAppService ! _
      }
    }
  }
  
  val serviceActor = system.actorOf(Props(new RootServiceActor))
  
  system.registerOnTermination {
	  logger.info("Root actor system shutting down...")
	  system.shutdown
  }
  
}
