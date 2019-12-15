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
  val webpack = "4.41.2"
  val webpackDevserver = "3.9.0"

  val react = "16.8.6"
  val fontAwesome = "5.11.2"
  val roshttp = "2.2.4"
  val upickle = "0.8.0"
  val fastParse = "2.1.3"

  val scalaTest = "3.0.8"
  val scalaMock = "4.4.0"
  val scalaTestPlay = "4.0.2"

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
  testOptions in Test +=
    (if (isScalaJSProject.value) Tests.Argument(TestFrameworks.ScalaTest)
    else Tests.Argument(TestFrameworks.ScalaTest,
      "-y", "org.scalatest.FunSpec")),
  logBuffered in Test := false,
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % versions.scalaTest % Test,
    "org.scalamock" %%% "scalamock" % versions.scalaMock % Test
  )
)

lazy val common = crossProject(JSPlatform, JVMPlatform).
  crossType(CrossType.Pure).
  in(file("common")).
  jsConfigure(project => project.enablePlugins(ScalaJSWeb)).
  settings(
    setScalaOptions,
    setTestOptions,

    libraryDependencies ++= Seq(
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
    setTestOptions,

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
    version in webpack := versions.webpack,
    version in startWebpackDevServer := versions.webpackDevserver,


    resolvers ++= Seq(
      Resolver.bintrayRepo("hmil", "maven")
    ),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % versions.scalajsDom,
      "org.scala-js" %%% "scalajs-java-time" % versions.scalaJsTime,
      "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,

      "fr.hmil" %%% "roshttp" % versions.roshttp,
    ),
    npmDependencies in Compile ++= Seq(
      "react" -> versions.react,
      "react-dom" -> versions.react
    ),
  )


lazy val server = project.in(file("server")).
  enablePlugins(PlayScala, GitVersioning, WebScalaJSBundlerPlugin).
  disablePlugins(PlayLayoutPlugin).
  dependsOn(common.jvm.settings(name := "commonJVM")).
  settings(
    setProject("Server"),
    setScalaOptions,
    setTestOptions,

    // connect the client project
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    // use fastOptJS for test and testonly as well.
    scalaJSPipeline / devCommands ++= Seq("test", "testOnly"),

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

      "com.mohiva" %% "play-silhouette" % versions.silhouette,
      "com.mohiva" %% "play-silhouette-password-bcrypt" % versions.silhouette,
      "com.mohiva" %% "play-silhouette-persistence" % versions.silhouette,

      "org.scalatestplus.play" %% "scalatestplus-play" % versions.scalaTestPlay,
	    "com.mohiva" %% "play-silhouette-testkit" % versions.silhouette % Test,
      // used for api tests
      "fr.hmil" %% "roshttp" % versions.roshttp
    )
  )


lazy val goodnight = project.in(file(".")).
  aggregate(common.jvm, common.js, client, server).
  settings(
    commands += Command.command("run")("server/run"::_)
  )
