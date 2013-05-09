package com.kolich.spray.auth

import scala.concurrent.{ Promise, ExecutionContext, Future }

import spray.routing._
import spray.routing.authentication._

import spray.util._
import spray.http._
import spray.http.HttpHeaders._

case class MissingSessionCookieRejection() extends Rejection
case class WebAppAuthenticationRejection() extends Rejection

trait WebAppAuthenticator[T] extends ContextAuthenticator[T] {

  def apply(ctx: RequestContext) = {
    val cookieHeaders = ctx.request.headers.findByType[`Cookie`]
    val cookies = cookieHeaders.map { case Cookie(cookie) => cookie }
    authenticate(cookies, ctx) map {
      case Some(sessionData) => Right(sessionData)
      case _ => Left {
        // No cookies were provided.
        if (cookieHeaders.isEmpty) MissingSessionCookieRejection()
        // Cookies were provided but validation of those session cookies failed.
        else WebAppAuthenticationRejection()
      }
    }
  }

  implicit def ec: ExecutionContext

  def authenticate(cookies: Option[Seq[HttpCookie]], ctx: RequestContext): Future[Option[T]]

}