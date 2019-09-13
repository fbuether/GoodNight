
package goodnight.community

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell


object Community {
  def render(router: RouterCtl[pages.Page]): VdomElement = {
    val content = ScalaComponent.builder.static("Community")(
      <.div("Community is not yet implemented.")).
      build

    Shell.component(Shell.Props(router,
      "Cloudy Night.png", "Have a Good Night"))(
      content())
  }
}
