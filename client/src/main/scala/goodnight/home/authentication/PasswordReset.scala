
package goodnight.home.authentication

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell


object PasswordReset {
  def render(router: RouterCtl[pages.Page]): VdomElement = {
    Shell.component(Shell.Props(router,
      "applications-accessories.png",
      "Password Reset"))(
      component(Props()))
  }

  case class Props(
  )

  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props): VdomElement =
      <.h2("Reset your password")
  }

  def component = ScalaComponent.builder[Props]("PasswordReset").
    stateless.
    renderBackend[Backend].
    build
}
