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

package com.kolich.spray.protocols

import com.kolich.spray.models._

import spray.json._
import spray.httpx._

private[protocols] trait WebAppJsonProtocol {
  
  object WebAppJsonFormat extends DefaultJsonProtocol with HtmlSafeSprayJsonSupport {
    
    implicit def ListResponseFormat[A: JsonFormat] = jsonFormat3(ListResponse.apply[A])
    implicit def ObjResponseFormat[A: JsonFormat]  = jsonFormat2(ObjResponse.apply[A])
    
    implicit object URIFormat extends RootJsonFormat[java.net.URI] {
    	def write(u: java.net.URI) = JsString(u.toString)
		def read(v: JsValue) = java.net.URI.create(v.toString)
    }
    
    implicit object ErrorFormat extends RootJsonFormat[Error] {
      def write(u: Error) = JsObject(
    	"success" -> JsBoolean(u.success),
    	"errorCode" -> JsNumber(u.errorCode),
    	"message" -> JsString(u.message.getOrElse(""))
      )
      def read(v: JsValue) = throw new DeserializationException("Read of models.Error not implemented")
    }
    
    implicit object UserFormat extends RootJsonFormat[User] {
      def write(u: User) = JsObject(
    	"name" -> JsString(u.name),
    	"email" -> JsString(u.email),
    	"phone" -> JsString(u.phone),
    	"notes" -> JsString(u.notes.getOrElse(""))
      )
      def read(v: JsValue) = throw new DeserializationException("Read of models.User not implemented")
    }
    
  }
  
}