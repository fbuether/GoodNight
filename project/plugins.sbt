// repository for Typesafe plugins
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.18")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.6.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.15")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
