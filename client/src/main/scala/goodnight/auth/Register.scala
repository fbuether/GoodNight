
package goodnight.auth

// import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.components.Shell
import goodnight.client.Page
import goodnight.client.Pages


object Register extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    Pages.Register.getRoute(dsl) ~> renderR(this.render)
  }

  def render(router: RouterCtl[Pages.Page]) =
    Shell.component(Shell.Props(router,
      "Cloudy Night.png",
      "Have a Good Night",
      <.div("Registration is not yet implemented.")))
}

