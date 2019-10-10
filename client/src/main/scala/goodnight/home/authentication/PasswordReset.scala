
package goodnight.home.authentication

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object PasswordReset {
  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router,
        "applications-accessories.png", "Password Reset"),
      component())

  // type Props = Unit
  // type State = Unit

  // class Backend(bs: BackendScope[Props, State]) {
  //   def render(p: Props): VdomElement =
  //     <.h2("Reset your password")
  // }

  val component = ScalaComponent.builder[Unit]("PasswordReset").
    render_(
      <.h2("Reset your password")).
    // stateless.
    // renderBackend[Backend].
    build
}
