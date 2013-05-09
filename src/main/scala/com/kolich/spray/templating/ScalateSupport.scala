package com.kolich.spray.templating

import org.fusesource.scalate.{ Binding, TemplateEngine }
import spray.http._
import spray.routing._
import MediaTypes._

// Totally borrowed from:
// https://github.com/spray/spray/blob/release-1.0.x/spray-routing/src/main/scala/spray/routing/directives/ScalateSupport.scala
// I was expecting this to be part of Spray 1.1-M7, but I couldn't find it there.
trait ScalateSupport {

  import Directives._

  private val templateEngine = new TemplateEngine

  def render(uri: String, attributes: Map[String, Any] = Map.empty,
    extraBindings: Traversable[Binding] = Nil,
    mediaType: MediaType = `text/html`): Route = {
    respondWithMediaType(mediaType) {
      complete {
        templateEngine.layout(uri, attributes, extraBindings)
      }
    }
  }

}