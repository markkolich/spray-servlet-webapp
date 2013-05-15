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

import java.lang.StringBuilder

import org.apache.commons.lang3.StringEscapeUtils.escapeHtml4

import spray.http._
import spray.httpx._
import spray.httpx.marshalling.Marshaller
import spray.json._

sealed trait HtmlSafeCompactJsonPrinter extends CompactPrinter {

  override def print(x: JsValue, sb: StringBuilder) {
    x match {
      case JsString(x) => printString(escapeHtml4(x), sb)
      case _ => super.print(x, sb)
    }
  }

}

object HtmlSafeCompactJsonPrinter extends HtmlSafeCompactJsonPrinter

trait HtmlSafeSprayJsonSupport extends SprayJsonSupport {

  override implicit def sprayJsonMarshaller[T](implicit writer: RootJsonWriter[T], printer: JsonPrinter = PrettyPrinter) =
    Marshaller.delegate[T, String](ContentType.`application/json`) { value =>
      lazy val printer = HtmlSafeCompactJsonPrinter
      val json = writer.write(value)
      printer(json)
    }

}