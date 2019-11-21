import sbt.Keys._
import sbt.Project.projectToRef
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

import java.util.Date
import java.text.SimpleDateFormat


def setProject(projName: String) = Seq(
  name := "GoodNight " + projName,
  maintainer := "fbuether@jasminefields.net",
)


val versions = new {
  val scala = "2.12.8"
  val scalajsDom = "0.9.7"
  val scalajsReact = "1.4.2"
  val scalaJsTime = "0.2.5"

  val react = "16.8.6"
  val reactMarkdown = "4.0.6"
  val reactMarkdownScala = "0.3.1"
  val fontAwesome = "5.11.2"
  val roshttp = "2.2.4"
  val upickle = "0.8.0"
  val fastParse = "2.1.3"

  val scalaTest = "3.0.8"
  val playSlick = "4.0.2"
  val postgresql = "42.2.6"
  val slickPostgres = "0.18.0"
  val silhouette = "6.1.0"
}



val setScalaOptions = Seq(
  scalaVersion := versions.scala,
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false
)


val setTestOptions = Seq(
  testOptions in Test += Tests.Argument(
    TestFrameworks.ScalaTest, "-y", "org.scalatest.FunSpec"),
  logBuffered in Test := false
)


lazy val common = crossProject(JSPlatform, JVMPlatform).
  crossType(CrossType.Pure).
  in(file("common")).
  jsConfigure(project => project.enablePlugins(ScalaJSWeb)).
  settings(
    setScalaOptions,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % versions.scalaTest % "test",
      "com.lihaoyi" %%% "fastparse" % versions.fastParse,
      "com.lihaoyi" %%% "upickle" % versions.upickle
    )
  )


lazy val client: Project = project.in(file("client")).
  enablePlugins(BuildInfoPlugin, GitVersioning,
    ScalaJSBundlerPlugin).
  dependsOn(common.js.settings(name := "commonJS")).
  settings(
    setProject("Client"),
    setScalaOptions,

    // store a set of build information.
    git.useGitDescribe := true,
    buildInfoPackage := "goodnight.version",
    buildInfoKeys := Seq[BuildInfoKey](
      name, version, scalaVersion, sbtVersion,
      BuildInfoKey.action("buildTime")(
        new SimpleDateFormat("yyyy-MM-dd HH:mm z").format(new Date())),
      git.gitHeadCommit,
      git.baseVersion,
    ),

    scalaJSUseMainModuleInitializer := true,

    // optimised building.
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    emitSourceMaps := false,
    version in webpack := "4.41.2",
    version in startWebpackDevServer := "3.9.0",


    resolvers ++= Seq(
      Resolver.bintrayRepo("hmil", "maven")
    ),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % versions.scalajsDom,
      "org.scala-js" %%% "scalajs-java-time" % versions.scalaJsTime,
      "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
      "com.dbrsn.scalajs.react.components" %%% "react-markdown" %
        versions.reactMarkdownScala,

      "fr.hmil" %%% "roshttp" % versions.roshttp,

    ),
    npmDependencies in Compile ++= Seq(
      "react" -> versions.react,
      "react-dom" -> versions.react,
      "react-markdown" -> versions.reactMarkdown
    ),
  )


lazy val server = project.in(file("server")).
  enablePlugins(PlayScala, GitVersioning, WebScalaJSBundlerPlugin).
  disablePlugins(PlayLayoutPlugin).
  dependsOn(common.jvm.settings(name := "commonJVM")).
  settings(
    setProject("Server"),
    setScalaOptions,

    // connect the client project
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),

    // less assets.
    includeFilter in (Assets, LessKeys.less) := "*.less",
    excludeFilter in (Assets, LessKeys.less) := "*.include.less",


    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.typesafeRepo("releases")
    ),

    libraryDependencies ++= Seq(
      // play cache
      caffeine,

      "org.webjars" % "font-awesome" % versions.fontAwesome,

      "com.typesafe.play" %% "play-slick" % versions.playSlick,
      "com.typesafe.play" %% "play-slick-evolutions" % versions.playSlick,
      "org.postgresql" % "postgresql" % versions.postgresql,
      "com.github.tminglei" %% "slick-pg" % versions.slickPostgres,
      "com.github.tminglei" %% "slick-pg_play-json" % versions.slickPostgres,

      "com.mohiva" %% "play-silhouette" % versions.silhouette,
      "com.mohiva" %% "play-silhouette-password-bcrypt" % versions.silhouette,
      "com.mohiva" %% "play-silhouette-persistence" % versions.silhouette,
    )
  )


lazy val goodnight = project.in(file(".")).
  aggregate(client, server).
  settings(
    commands += Command.command("run")({ state =>
      "server/run" :: state
    })
  )
