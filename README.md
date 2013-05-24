# spray-servlet-webapp

Hi, you've stumbled across my web-application example using <a href="http://spray.io">Spray</a> and <a href="http://spray.io/documentation/spray-servlet/">spray-servlet</a> 1.1-M7.

On a recent project, I spent quite a bit of time plugging together a production web-application built around Spray.  Although the stand-up went fairly well, I encountered several pain points while building a "place to stand" (connecting the Spray dots) &mdash; setting up the project, figuring out how to package and serve static content, integrating Scalate template support, authentication and cookie based session support, integrating JSON support, etc.  To that end, I thought it would be helpful to spend some time hacking out a complete "real world" Spray web-application example.  The Spray documentation is excellent, and this project is intended to supplement that documentation with a complete and robust example that illustrates how a number of Spray concepts integrate into a web-application.

Note, this example project uses `spray-servlet` and ultimately compiles and packages the web-application into a `.war` file, deployable in most Servlet 3.0 containers.  Of course, if you're not using a Servlet container to deploy your application, you can certainly extend the concepts herein to your server of choice &mdash; perhaps something like `spray-can`.

Feedback and pull requests always welcome.

## What this Example Covers

* SBT and "in project" Servlet container integration using the <a href="https://github.com/JamesEarlDouglas/xsbt-web-plugin">xsbt-web-plugin</a> and Jetty 8.1.10.v20130312.  See <a href="https://github.com/markkolich/spray-servlet-webapp/blob/master/project/Build.scala">Build.scala</a>.

* The `spray-servlet` configuration of multiple `HttpServiceActor`'s under a single root actor. One actor deals with vanilla web-application requests, while another is dedicated to dealing with JSON AJAX requests. See <a href="https://github.com/markkolich/spray-servlet-webapp/blob/master/src/main/scala/com/kolich/spray/Boot.scala">Boot.scala</a>.

