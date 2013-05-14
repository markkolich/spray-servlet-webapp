package com.kolich.spray.protocols

import spray.json._
import spray.httpx._

trait WebAppJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  
  object WebAppJsonFormat {
    
    implicit object URIFormat extends RootJsonFormat[java.net.URI] {
    	def write(u: java.net.URI) = JsString(u.toString)
		def read(value: JsValue) = java.net.URI.create(value.toString)
    }
    
  }
  
}

