
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages


object Loading {
  val component = ScalaComponent.builder[pages.Router]("Loading").
    render_P({ router =>
      <.div(^.className := "loadingBanner",
        Image.component(router, "Less Boring Clock.png"),
        <.span("Loading…"))
    }).
    build

  def suspend(router: pages.Router, el: AsyncCallback[VdomElement]) =
    React.Suspense(component(router), el)
}


