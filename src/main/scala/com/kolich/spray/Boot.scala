package com.kolich.spray

import com.kolich.spray.service._

import akka.actor.{Props, ActorSystem}
import com.weiglewilczek.slf4s.{Logging, Logger}
import spray.routing._
import spray.servlet._

class Boot extends WebBoot with Logging {
  
  val system = ActorSystem("spray-servlet-webapp")
  
  val serviceActor = system.actorOf(Props[WebAppService])
  
}
