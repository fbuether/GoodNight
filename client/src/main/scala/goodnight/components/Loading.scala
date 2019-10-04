
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages


object Loading {
  val component = ScalaComponent.builder[RouterCtl[pages.Page]]("Loading").
    render_P({ router =>
      <.div(^.className := "loadingBanner",
        <.img(^.src := (router.baseUrl +
          "assets/images/buuf/Less Boring Clock.png").value),
        <.span("Loading..."))
    }).
    build


  def suspend(router: RouterCtl[pages.Page], el: AsyncCallback[VdomElement]) =
    React.Suspense(component(router), el)
}


