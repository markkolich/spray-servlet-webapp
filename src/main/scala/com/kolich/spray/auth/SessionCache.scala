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

import java.util.UUID._
import java.util.concurrent.TimeUnit

import com.kolich.common.util.crypt.Base64Utils.encodeBase64URLSafe

import com.google.common.cache.CacheBuilder

import org.apache.commons.codec.binary.Base64._
import org.apache.commons.codec.binary.StringUtils._

import com.kolich.spray._
import com.kolich.spray.models._

// In memory session cache, backed by Google's CacheBuilder.
sealed trait SessionCache {

  private lazy val sessionCache = CacheBuilder.newBuilder()
    .expireAfterAccess(ApplicationConfig.sessionTimeout, TimeUnit.MILLISECONDS)
    .asInstanceOf[CacheBuilder[String, SessionData]]
    .build[String, SessionData]()
    .asMap() // Concurrent map, fwiw

  def getSession(sessionId: String): Option[SessionData] =
    Option(sessionCache.get(sessionId)) // Wrapped in Option() to handle null's

  def setSession(sessionId: String, data: SessionData): Option[SessionData] =
    Option(sessionCache.put(sessionId, data)) // Wrapped in Option() to handle null's

  def removeSession(sessionId: String): Option[SessionData] =
    Option(sessionCache.remove(sessionId)) // Wrapped in Option() to handle null's

  def getRandomSessionId: String = {
    // Base-64 URL safe encoding (for the cookie value) and no chunking of
    // the encoded output (important).
    encodeBase64URLSafe(randomUUID.toString)
  }

}

object SessionCache extends SessionCache