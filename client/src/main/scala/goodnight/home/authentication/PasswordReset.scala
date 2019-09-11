
package goodnight.home.authentication

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.Page
import goodnight.client.Pages
import goodnight.components.Shell


object PasswordReset extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    Pages.RequestPasswordReset.getRoute(dsl) ~>
    renderR(r => this.component(Props(r)))
  }

  case class Props(
    router: RouterCtl[Pages.Page]
  )

  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props): VdomElement =
      Shell.component(Shell.Props(p.router,
        "applications-accessories.png",
        "Password Reset"))(
        <.h2("Reset your password"))
  }

  def component = ScalaComponent.builder[Props]("PasswordReset").
    stateless.
    renderBackend[Backend].
    build
}
