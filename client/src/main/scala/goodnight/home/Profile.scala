
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object Profile {
  def render(router: RouterCtl[pages.Page]): VdomElement = {
    Shell.component(Shell.Props(router,
      ))(
      component(Props(router)))
  }


  // type Props = (RouterCtl[pages.Page])

  case class Props(
    router: RouterCtl[pages.Page]
  )

  case class State(
    i: Int
  )

  class Backend(bs: BackendScope[Props, State]) {
    val changer = bs.modState(s => s.copy(i = s.i + 1))

    def render(p: Props, s: State): VdomElement =
      <.div(
        <.h2("Profile"),
        <.p(
          p.router.link(pages.Profile)(
            "go to your profile.")),
        <.p("Hello there."),
        <.p(
          <.a(^.onClick --> changer,
            "wooah")))
  }

  val component = ScalaComponent.builder[Props]("Profile").
    initialState(State(2)).
    renderBackend[Backend].
    build

  def render(router: RouterCtl[pages.Page]) =
    Shell.component(router)(
      Banner.component(router,
        "Excuse me, that's just the Henny man....png", "Profile"),
      this.component())
}
