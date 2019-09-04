import sbt.Keys._
import sbt.Project.projectToRef

import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}


def setProject(projName: String) = Seq(
  name := "GoodNight " + projName,
  version := "0.0.4",
  maintainer := "fbuether@jasminefields.net",
)


val versions = new {
  val scala = "2.12.8"
  val scalajsDom = "0.9.7"
  val scalajsReact = "1.4.2"
  val log4js = "1.4.13-1"
  // val autowire = "0.2.6"
  // val booPickle = "1.3.1"
  // val diode = "1.1.5"
  // val diodeReact = "1.1.5.142"
  val uTest = "0.7.1"
  val scalaJsTime = "0.2.5"

  val react = "16.8.6"
  val jQuery = "3.4.1"
  val fontawesome = "5.6.3"
  // val bootstrap = "4.3.1"
  // val chartjs = "2.6.0"

  val scalajsScripts = "1.1.2"
}


val setScalaOptions = Seq(
  scalaVersion := versions.scala,
  scalacOptions ++= Seq(
    "-Xlint:_",
    "-unchecked",
    "-deprecation",
    "-feature",
  )
)


// scalac allows to elide code in certain builds, to remove debug
// behaviour from production builds. This setting controlls that
// behaviour.
lazy val elideOptions = settingKey[Seq[String]](
  "Set limit for elidable functions")


lazy val common = crossProject(JSPlatform, JVMPlatform).
  crossType(CrossType.Pure).
  in(file("common")).
  jsConfigure(project => project.enablePlugins(ScalaJSWeb)).
  settings(
    setScalaOptions,
    libraryDependencies ++= Seq(
      // "com.lihaoyi" %%% "autowire" % versions.autowire,
      // "io.suzaku" %%% "boopickle" % versions.booPickle
    )
  )


lazy val client: Project = project.in(file("client")).
  enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(common.jvm.settings(name := "commonJS")).
  settings(
    setProject("Client"),
    setScalaOptions,

    // by default, do not elide anything, i.e. keep everything.
    elideOptions := Seq(),
    scalacOptions ++= elideOptions.value,

    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true,

    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,

      "org.scala-js" %%% "scalajs-dom" % versions.scalajsDom,
      "org.scala-js" %%% "scalajs-java-time" % versions.scalaJsTime,

      // "io.suzaku" %%% "diode" % versions.diode,
      // "io.suzaku" %%% "diode-react" % versions.diodeReact,

      "com.lihaoyi" %%% "utest" % versions.uTest % Test
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "react" % versions.react /
        "umd/react.development.js"
        minified "umd/react.production.min.js"
        commonJSName "React",
      "org.webjars.npm" % "react-dom" % versions.react /
        "umd/react-dom.development.js"
        minified "umd/react-dom.production.min.js"
        dependsOn "umd/react.development.js"
        commonJSName "ReactDOM",
      "org.webjars.npm" % "react-dom" % versions.react /
        "umd/react-dom-server.browser.development.js"
        minified "umd/react-dom-server.browser.production.min.js"
        dependsOn "umd/react-dom.development.js"
        commonJSName "ReactDOMServer",
      "org.webjars.npm" % "jquery" % versions.jQuery /
        "dist/jquery.js"
        minified "jquery.min.js",
      // "org.webjars.npm" % "bootstrap" % versions.bootstrap /
      // "bootstrap.js" minified "bootstrap.min.js" dependsOn "dist/jquery.js",
      // "org.webjars.bower" % "chartjs" % versions.chartjs /
      // "Chart.js" minified "Chart.min.js",
      "org.webjars" % "log4javascript" % versions.log4js /
        "js/log4javascript_uncompressed.js"
        minified "js/log4javascript.js"
    ),

    // settings for tests.
    scalaJSUseMainModuleInitializer in Test := false,
    // RuntimeDOM is needed for tests
    jsEnv in Test := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv,
    testFrameworks += new TestFramework("utest.runner.Framework"),

    // react 16.8 does not properly anounce dependencies at webjars.org,
    // add these here manually. https://github.com/webjars/webjars/issues/1789
    dependencyOverrides ++= Seq(
      "org.webjars.npm" % "js-tokens" % "4.0.0",
      "org.webjars.npm" % "scheduler" % "0.14.0"
    )
  )


lazy val server = project.in(file("server")).
  enablePlugins(PlayScala).
  disablePlugins(PlayLayoutPlugin).
  aggregate(projectToRef(client)).
  dependsOn(common.jvm.settings(name := "commonJVM")).
  settings(
    setProject("Server"),
    setScalaOptions,

    // trigger scalaJSPipeline when using compile or continuous compilation
    compile in Compile := compile.in(Compile).dependsOn(scalaJSPipeline).value,
    // connect the client project
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(rjs, digest, gzip),

    // less assets.
    includeFilter in (Assets, LessKeys.less) := "*.less",
    excludeFilter in (Assets, LessKeys.less) := "*.include.less",
    LessKeys.compress in Assets := true,

    // new command to build a release version, also runs tests.
    commands += Command.command("release") { state =>
      "set elideOptions in client := Seq(\"-Xelide-below\", \"WARNING\")" ::
      "client/clean" ::
      "client/test" ::
      "server/clean" ::
      "server/test" ::
      "server/dist" ::
      "set elideOptions in client := Seq()" ::
      state
    },

    libraryDependencies ++= Seq(
      guice, // injection for play

      "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
      "org.webjars.npm" % "fontawesome" % versions.fontawesome % Provided,
      // "org.webjars.npm" % "bootstrap" % versions.bootstrap % Provided,

      "com.lihaoyi" %% "utest" % versions.uTest % Test,
    )
  )


// load the Play server project at sbt startup
onLoad in Global := (Command.
  process("project server", _: State)).
  compose((onLoad in Global).value)
