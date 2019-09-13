
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell


object Profile {
  def render(router: RouterCtl[pages.Page]): VdomElement = {
    Shell.component(Shell.Props(router,
      "Excuse me, that's just the Henny man....png", "Profile"))(
      component(Props(router)))
  }


  case class Props(
    router: RouterCtl[pages.Page]
  )

  case class State(
  )

  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement =
      <.div(
        <.h2("Profile"),
        <.p(
          p.router.link(pages.Profile)(
            "go to your profile.")),
        <.p("Hello there."),
        <.p(
          <.a(^.onClick ==> p.router.setEH(pages.Profile),
            "wooah")))
  }

  def component = ScalaComponent.builder[Props]("Profile").
    initialState(State()).
    renderBackend[Backend].
    build
}
