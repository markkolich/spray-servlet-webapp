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
    .expireAfterAccess(ApplicationConfig.sessionTimeout.toMillis, TimeUnit.MILLISECONDS)
    .asInstanceOf[CacheBuilder[String, SessionData]]
    .build[String, SessionData]()
    .asMap() // Concurrent map, ftw

  def getSession(sessionId: String):Option[SessionData] =
    sessionCache.get(sessionId) match {
    	case session:SessionData => Some(session)
    	case _ => None // for null from Guava cache (Java land)
  	}

  def setSession(sessionId: String, data: SessionData):Option[SessionData] =
    sessionCache.put(sessionId, data) match {
    	case session:SessionData => Some(session)
    	case _ => None // for null from Guava cache (Java land)
  	}

  def removeSession(sessionId: String):Option[SessionData] =
    Some(sessionCache.remove(sessionId))

  def getRandomSessionId: String = {
    // Base-64 URL safe encoding (for the cookie value) and no chunking of
    // the encoded output (important).
    encodeBase64URLSafeString(getBytesUtf8(randomUUID.toString))
  }

}