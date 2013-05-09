package com.kolich.spray.auth

import java.util.UUID._
import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder

import org.apache.commons.codec.binary.Base64._
import org.apache.commons.codec.binary.StringUtils._

import com.kolich.spray._
import com.kolich.spray.models._

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
    encodeBase64URLSafeString(getBytesUtf8(randomUUID.toString))
  }

}

object SessionCache extends SessionCache