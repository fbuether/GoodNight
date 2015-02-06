import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

/**
 * Application settings. Configure the build for your application here.
 * You normally don't have to touch the actual build definition after this.
 */
object Settings {
  /** The name of your application */
  val name = "scalajs-spa"

  /** The version of your application */
  val version = "1.1.5"

  /** Options for the scala compiler */
  val scalacOptions = Seq(
    "-Xlint",
    "-unchecked",
    "-deprecation",
    "-feature"
  )

  /** Declare global dependency versions here to avoid mismatches in multi part dependencies */
  object versions {
    val scala = "2.11.11"
    val scalaDom = "0.9.3"
    val scalajsReact = "1.1.0"
    val scalaCSS = "0.5.3"
    val log4js = "1.4.10"
    val autowire = "0.2.6"
    val booPickle = "1.2.6"
    val diode = "1.1.2"
    val uTest = "0.4.7"

    val react = "15.6.1"
    val jQuery = "1.11.1"
    val bootstrap = "3.3.6"
    val chartjs = "2.1.3"

    val scalajsScripts = "1.0.0"
  }

  /**
   * These dependencies are shared between JS and JVM projects
   * the special %%% function selects the correct version for each project
   */
  val sharedDependencies = Def.setting(Seq(
    "com.lihaoyi" %%% "autowire" % versions.autowire,
    "io.suzaku" %%% "boopickle" % versions.booPickle
  ))

  /** Dependencies only used by the JVM project */
  val jvmDependencies = Def.setting(Seq(
    "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
    "org.webjars" % "font-awesome" % "4.3.0-1" % Provided,
    "org.webjars" % "bootstrap" % versions.bootstrap % Provided,
    "com.lihaoyi" %% "utest" % versions.uTest % Test
  ))

  /** Dependencies only used by the JS project (note the use of %%% instead of %%) */
  val scalajsDependencies = Def.setting(Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
    "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
    "io.suzaku" %%% "diode" % versions.diode,
    "io.suzaku" %%% "diode-react" % versions.diode,
    "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
    "com.lihaoyi" %%% "utest" % versions.uTest % Test
  ))

  /** Dependencies for external JS libs that are bundled into a single .js file according to dependency order */
  val jsDependencies = Def.setting(Seq(
    "org.webjars.npm" % "react" % v.react / "umd/react.development.js" minified "umd/react.production.min.js" commonJSName "React",
    "org.webjars.npm" % "react-dom" % v.react / "umd/react-dom.development.js" minified "umd/react-dom.production.min.js" dependsOn "umd/react.development.js" commonJSName "ReactDOM",
    "org.webjars.npm" % "react-dom" % v.react / "umd/react-dom-server.browser.development.js" minified "umd/react-dom-server.browser.production.min.js" dependsOn "umd/react-dom.development.js" commonJSName "ReactDOMServer",
    "org.webjars.npm" % "jquery" % v.jQuery / "dist/jquery.js" minified "jquery.min.js",
    "org.webjars.npm" % "bootstrap" % v.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "dist/jquery.js",
    "org.webjars.bower" % "chartjs" % v.chartjs / "Chart.js" minified "Chart.min.js",
    "org.webjars" % "log4javascript" % v.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
  ))

  /** Fixes unresolved deps issue: https://github.com/webjars/webjars/issues/1789 **/
  val dependencyOverrides = Def.setting(Seq(
    "org.webjars.npm" % "js-tokens" % "4.0.0",
    "org.webjars.npm" % "scheduler" % "0.14.0"
  ))
}
