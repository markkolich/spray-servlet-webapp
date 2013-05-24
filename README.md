spray-servlet-webapp
====================

Hi, you've stumbled across my web-application example using Spray and `spray-servlet` 1.1-M7.

I've spent a lot of time plugging together a production web-application built around Spray and `spray-servlet`.  I enjoy using Spray and appreciate its integration with Akka, but I found Spray's documentation a bit incomplete with regards to real world "this is how you do X" scenarios and examples.  To that end, I thought it would be useful to spend some time hacking out a complete "real world" Spray web-application example, deployable in any modern Servlet 3.0 container.  Of course, if you're not using a Servlet container to deploy your application, you can certainly extend the concepts herein to your server of choice &mdash; perhaps something like `spray-can`.
