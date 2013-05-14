package com.kolich.spray.service

import akka.actor._
import spray.routing._

import com.kolich.spray.auth._
import com.kolich.spray.templating._
import com.kolich.spray.protocols._

import com.weiglewilczek.slf4s.{Logging, Logger}

trait Controller
	extends Actor
	with HttpServiceActor
	with Secure
	with ScalateSupport
	with JsonProtocols
	with Logging