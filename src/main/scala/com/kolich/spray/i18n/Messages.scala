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

package com.kolich.spray.i18n

import java.util.{ResourceBundle, Locale}
import java.text.MessageFormat

import collection.{Iterator, Map}
import collection.JavaConversions._
import collection.mutable.HashMap

sealed trait MessageResolver extends Map[String, String] {

  def -(key: String): Map[String, String] = this
  def +[B1 >: String](kv: (String, B1)): Map[String, B1] = this

  def getLocale: Locale
  def resolve(key: String): Option[String]

  def resolveRange(key: String): Option[String] = resolve(key) orElse {
    if(!key.contains(".")) {
      None
    } else {
      resolveRange(key.substring(key.indexOf(".") + 1))
    }
  }

  def get(key: String): Option[String] = resolveRange(key) orElse None

  def fmt(key: String, params: (String, Any)*): String = params.foldLeft(getOrElse(key, key)) {
    (result, p) => result.replaceAll("\\{" + p._1 + "\\}", p._2.toString)
  }

  def format(key: String, params: AnyRef*): String = MessageFormat.format(getOrElse(key, key), params: _*)

}

class ResourceBundleMessageResolver(
    val bundleName:String = ("i18n/messages"),
    val locale:Any = new Locale("en", "US")
  ) extends MessageResolver {

  private def bundle = ResourceBundle.getBundle(bundleName, getLocale)

  def iterator: Iterator[(String, String)] = bundle.getKeys.map(k => (k -> bundle.getString(k)))

  def getLocale: Locale = locale match {
    case Some(l: Locale) => l
    case Some(l: String) => new Locale(l)
    case _ => Locale.getDefault
  }

  def resolve(key: String): Option[String] = try {
    Some(bundle.getString(key))
  } catch {
    case e: Exception => None
  }

}

case class Msg(key: String, params: (String, Any)*) {
  def param(key: String): Option[Any] = params.find(_._1 == key).map(_._2)
  def hasParam(key: String): Boolean = !params.find(_._1 == key).isEmpty
}
