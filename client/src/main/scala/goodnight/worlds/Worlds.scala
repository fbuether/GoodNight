
package goodnight.worlds

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object Worlds {
  val component = ScalaComponent.builder.static("Worlds")(
    <.div(
      <.h2("Available Worlds"),
      <.p("""Worlds in GoodNight exist within a universe. Worlds
          from the same universe share a common theme, maybe even
          characters and locations. The following shows all universes
          and the worlds within."""))).
    build

  def render(router: RouterCtl[pages.Page]) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "The Worlds"),
      this.component())
}
