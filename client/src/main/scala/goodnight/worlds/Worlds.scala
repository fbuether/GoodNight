
package goodnight.worlds

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell


object Worlds {
  def render(router: RouterCtl[pages.Page]): VdomElement = {
    val content = ScalaComponent.builder.static("Worlds")(
    <.div(
      <.h3("Available Worlds"),
      <.p("""Worlds in GoodNight exist within a universe. Worlds
          from the same universe share a common theme, maybe even
          characters and locations. The following shows all universes
          and the worlds within."""))).
      build

    Shell.component(Shell.Props(router,
      "Alien World.png", "The Worlds"))(
      content())
  }
}
