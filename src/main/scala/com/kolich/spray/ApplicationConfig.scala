package com.kolich.spray

import com.typesafe.config.{ ConfigFactory, Config }
import scala.concurrent.duration._

object ApplicationConfig {
  
  private[this] val c:Config = {
    val c = ConfigFactory.load()
    c.checkValid(ConfigFactory.defaultReference(), "webapp.demo")
    c.getConfig("webapp.demo")
  }
  
  final lazy val sessionTimeout:Duration = Duration(c getString "session-timeout")

}