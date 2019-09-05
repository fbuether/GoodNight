
package goodnight.client

import org.scalajs.dom.document

// import japgolly.scalajs.react._
// import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router._
// import japgolly.scalajs.react.vdom.Implicits._


object Client {
  private def pages: List[Page] = List(
    goodnight.client.Home,
    goodnight.worlds.Worlds,
    goodnight.client.Community,
    goodnight.client.Register,
    goodnight.client.SignIn)

  def main(args: Array[String]): Unit = {
    val routerConfig = RouterConfigDsl[Pages.Page].buildConfig(dsl => {
      import dsl._
      this.pages.map(_.route(dsl)).
        foldLeft(emptyRule)(_ | _).
        noFallback.
        // notFound(<.div("404"))
        notFound(redirectToPage(Pages.Home)(Redirect.Replace)).
        logToConsole
    })
    val router = Router(BaseUrl.fromWindowOrigin / "#/", routerConfig)
    val rootElement = document.getElementById("goodnight-client")
    router.ctor().renderIntoDOM(rootElement)
  }
}


