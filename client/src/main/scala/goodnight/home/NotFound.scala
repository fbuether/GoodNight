
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router._

import goodnight.client.Page
import goodnight.client.PageDescriptor
import goodnight.components.Shell


object NotFound {
  case class NotFoundPage(path: String) extends Page

  def page = new PageDescriptor {
    def getRoute(dsl: Dsl): Route = {
      import dsl._
      dynamicRouteCT((dsl.root ~ remainingPathOrBlank).
        caseClass[NotFoundPage]) ~>
      dynRender(p => component(Props(p.path)))
    }
  }

  case class Props(
    path: String
  )

  case class State()

  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement =
      <.div(
        <.h2("Welcome!"),
        <.p("We haven't found what you look for, sorry."),
        <.p("Debug info: Invalid path \"" + p.path + "\"."))
  }

  def component = ScalaComponent.builder[Props]("Home").
    initialState(State()).
    renderBackend[Backend].
    build
}
