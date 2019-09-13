
package goodnight.worlds

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell


object World {
  def render(page: pages.World, router: RouterCtl[pages.Page]) = {
    val content = ScalaComponent.builder.static("Not Found")(
      <.div(
        <.h2("Welcome!"),
        <.p("""Worlds in GoodNight exist within a universe. Worlds
          from the same universe share a common theme, maybe even
          characters and locations. The following shows all universes
          and the worlds within."""),
        <.h3("The World"),
        <.p("You have found the world " + page.name + "."))).
      build

    Shell.component(Shell.Props(router, "Lock.png", page.name))(content())
  }

}
