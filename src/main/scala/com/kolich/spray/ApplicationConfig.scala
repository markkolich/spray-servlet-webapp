package com.kolich.spray

import com.typesafe.config.{ ConfigFactory, Config }

object ApplicationConfig {

  val c = ConfigFactory.load()

  private[this] val appConfig: Config = {
    c.checkValid(ConfigFactory.defaultReference(), "webapp.demo")
    c.getConfig("webapp.demo")
  }

  private[this] val sprayConfig: Config = {
    c.checkValid(ConfigFactory.defaultReference(), "spray.servlet")
    c.getConfig("spray.servlet")
  }

  final lazy val sessionTimeout: Long = appConfig getMilliseconds "session-timeout"

  final lazy val rootPath: String = sprayConfig getString "root-path" 

}