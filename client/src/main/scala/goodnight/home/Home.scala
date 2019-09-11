
package goodnight.home

// import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.components.Shell


object Home extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    Pages.Home.getRoute(dsl) ~> renderR(this.render)
  }

  def render(router: RouterCtl[Pages.Page]): VdomElement =
    Shell.component(Shell.Props(router,
      "Cloudy Night.png",
      "Have a Good Night"))(
      <.div(
        <.h2("Welcome!"),
        <.p("""GoodNight is the home of many wonderous adventures.
          Experience the tension of diving into the ocean's depths,
          climbing the stars, or fighting for your survival in the
          cold arctic."""),
        <.div(^.className := "withColumns",
          <.div(
            <.h3("News"),
            <.p("GoodNight is in development! New things are bound to appear " +
              "any time.")),
          <.div(
            <.h3("Available Worlds"),
            <.ul(^.className := "worldList",
              // WorldList()
            )))))
}
