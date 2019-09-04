
package goodnight.client

// import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.components.Shell


object Register extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    staticRoute("#" / "register", Pages.Register) ~> renderR(this.render)
  }

  def render(router: RouterCtl[Pages.Page]) =
    Shell.component(Shell.Props(router,
      "Cloudy Night.png",
      "Have a Good Night",
      <.div("Registration is not yet implemented.")))
}

