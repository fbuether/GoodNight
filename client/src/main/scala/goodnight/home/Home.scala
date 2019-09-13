
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages.Page
import goodnight.components.Shell


object Home {
  def render(router: RouterCtl[Page]): VdomElement = {
    val content = ScalaComponent.builder.static("Home")(
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

    Shell.component(Shell.Props(router,
      "Cloudy Night.png", "Have a Good Night"))(
      content())
  }
}
