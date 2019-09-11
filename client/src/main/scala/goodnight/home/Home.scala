
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.extra.router.StaticDsl._

import goodnight.components.Shell
import goodnight.client.Page
import goodnight.client.StaticPageDescriptor


object Home {
  case object HomePage extends Page

  def page = new StaticPageDescriptor {
    def route(dsl: Dsl) = dsl.root
    def showPage(dsl: Dsl) = dsl.render(component(Props()))
    val getPage = HomePage
  }

  case class Props()

  case class State()

  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement =
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
            ))))
  }

  def component = ScalaComponent.builder[Props]("Home").
    initialState(State()).
    renderBackend[Backend].
    build
}
