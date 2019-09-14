
package goodnight.community

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object Community {
  val component = ScalaComponent.builder.static("Community")(
    <.div("Community is not yet implemented.")).
    build


  def render(router: RouterCtl[pages.Page]) =
    Shell.component(router)(
      Banner.component(router,
        "An esculator can never break, it can only become stairs..png",
        "Community"),
      this.component())
}
