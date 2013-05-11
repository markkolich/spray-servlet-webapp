package com.kolich.spray.models

case class SessionData(id: String, username: String) {
  override def toString = "(id=" + id + ", username=" + username + ")" 
}