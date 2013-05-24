# spray-servlet-webapp

Hi, you've stumbled across my web-application example using <a href="http://spray.io">Spray</a> and <a href="http://spray.io/documentation/spray-servlet/">spray-servlet</a> 1.1-M7.

On a recent project, I spent quite a bit of time plugging together a production web-application built around Spray.  Although the stand-up went fairly well, I encountered several pain points while building a "place to stand" (connecting the Spray dots) &mdash; setting up the project, figuring out how to package and serve static content, integrating Scalate templating support, integrating authentication and cookie based session support, integrating JSON support, etc.  To that end, I thought it would be helpful to spend some time hacking out a complete "real world" Spray web-application example.

The Spray documentation is excellent, and this project is intended to supplement that documentation with a complete and robust example that illustrates how a number of Spray concepts integrate into a web-application.

Note, this example project uses `spray-servlet` and ultimately compiles and packages the web-application into a `.war` file, deployable in most Servlet 3.0 containers.  Of course, if you're not using a Servlet container to deploy your application, you can certainly extend the concepts herein to your server of choice &mdash; perhaps something like `spray-can`.

Feedback and pull requests always welcome.

## What this Example Covers

* SBT "in project" Servlet container integration using the <a href="https://github.com/JamesEarlDouglas/xsbt-web-plugin">xsbt-web-plugin</a> and Jetty 8.1.10.v20130312.  See <a href="https://github.com/markkolich/spray-servlet-webapp/blob/master/project/Build.scala">Build.scala</a>.

* The `spray-servlet` WebBoot configuration of multiple `HttpServiceActor`'s under a single root actor. One actor deals with vanilla web-application requests, while another is dedicated to dealing with JSON AJAX requests. See <a href="https://github.com/markkolich/spray-servlet-webapp/blob/master/src/main/scala/com/kolich/spray/Boot.scala">Boot.scala</a>.

* The serving and packaging of static web-application content: JavaScript, images, and CSS.  As far as I can tell, the Spray default mechanism for serving static content involves packaging such resources into a `.jar` file, and consequently serving static content from that `.jar` on the classpath.  This became frustrating very quickly when I realized, for example, that I could not simply modify a `.js` JavaScript file and refresh the page in my browser to see the change &mdash; because the static content was bundled into a resource `.jar` I had to stop my application and restart the JVM to see any local changes to static content.  This was a showstopper for rapid development, so I gave up and figured out how to get the Servlet container's *default servlet* to serve static content.  I did try to use tools like <a href="https://github.com/spray/sbt-revolver">sbt-revolver</a>, but that failed miserably (I probably just didn't know what I was doing).  See <a href="https://github.com/markkolich/spray-servlet-webapp/tree/master/src/main/webapp/public">public</a>.

* Full <a href="http://scalate.fusesource.org">Scalate</a> templating support, which demonstrates how to use Scalate templates and pass data from your Spray routes to a Scalate template.  See <a href="https://github.com/markkolich/spray-servlet-webapp/blob/master/src/main/scala/com/kolich/spray/templating/ScalateSupport.scala">ScalateSupport.scala</a> and <a href="https://github.com/markkolich/spray-servlet-webapp/tree/master/src/main/resources/templates">templates</a>.

* Straightforward user authentication using Spray's `authenticate` directive with cookie based session support.  Like you would expect, the user has to be "logged in" to access specific paths and content, which is managed here using session cookies and an in-memory session map.  See the <a href="https://github.com/markkolich/spray-servlet-webapp/tree/master/src/main/scala/com/kolich/spray/auth">com.kolich.spray.auth</a> package.

* Realistic JSON support, which demonstrates how to use <a href="https://github.com/spray/spray-json">spray-json</a> to unmarshall JSON into case classes (on requests), and marshall case classes back into JSON (for responses).  See the <a href="https://github.com/markkolich/spray-servlet-webapp/tree/master/src/main/scala/com/kolich/spray/protocols">com.kolich.spray.protocols</a> package.
