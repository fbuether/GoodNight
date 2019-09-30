
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object Story {


  val component = ScalaComponent.builder[String]("World").
    render_P(name =>
      <.div(
        <.h2("Welcome!"),
        <.p("""Worlds in GoodNight exist within a universe. Worlds
          from the same universe share a common theme, maybe even
          characters and locations. The following shows all universes
          and the worlds within."""),
        <.h3("The World"),
        <.p("You have found the world \"" + name + "\"."))).
    build

  def render(page: pages.Story, router: RouterCtl[pages.Page]) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "A world"),
      this.component(page.name))
}
