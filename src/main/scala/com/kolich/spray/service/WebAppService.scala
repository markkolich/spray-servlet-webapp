package com.kolich.spray.service

import scala.concurrent.duration._

import com.weiglewilczek.slf4s.{Logging, Logger}

import com.kolich.spray.templating._

import akka.actor._
import spray.util._
import spray.http._
import spray.routing._
import MediaTypes._
import HttpMethods._

class WebAppService extends Actor with HttpServiceActor with ScalateSupport with Logging {
  
  @Override
  def receive = runRoute {
    path("") {
      get {
        render("templates/index.ssp")
      }
    } ~
    path ("login") {
      get {
        render("templates/login.ssp")
      } ~
      post {
        complete("Meh")
      }
    }
  }

}