
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object Home {
  val component = ScalaComponent.builder[RouterCtl[pages.Page]]("Home").
    render_P(router =>
      <.div(
        <.h2("Welcome!"),
        <.p("""GoodNight is the home of many wonderous adventures.
          Experience the tension of diving into the ocean's depths,
          climbing the stars, or fighting for your survival in the
          cold arctic."""),
        <.div(^.className := "withColumns",
          <.div(
            <.h3("News"),
            <.p("GoodNight is in development! New things are bound to " +
              "appear any time.")),
          <.div(
            <.h3("Available Worlds"),
            <.ul(^.className := "worldList",
              // WorldList()
            ))))).
    build


  def render(router: RouterCtl[pages.Page]) =
    Shell.component(router)(
      Banner.component(router, "Cloudy Night.png", "Have a Good Night"),
      this.component(router))
}
