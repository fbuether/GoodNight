
package goodnight.worlds

// import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.Page
import goodnight.client.Pages
import goodnight.components.Shell


object Worlds extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    Pages.Worlds.getRoute(dsl) ~> renderR(this.render)
  }

  def render(router: RouterCtl[Pages.Page]): VdomElement =
    Shell.component(Shell.Props(router,
      "Alien World.png", "The Worlds",
      <.div(
        <.h3("Available Worlds"),
        <.p("""Worlds in GoodNight exist within a universe. Worlds
          from the same universe share a common theme, maybe even
          characters and locations. The following shows all universes
          and the worlds within."""))))
}
