package com.kolich.spray

import com.kolich.spray.service.WebAppService
import com.weiglewilczek.slf4s.Logging

import akka.actor.ActorSystem
import akka.actor.Props
import spray.servlet.WebBoot

class Boot extends WebBoot with Logging {

  val system = ActorSystem("spray-servlet-webapp")

  val serviceActor = system.actorOf(Props[WebAppService])

}
