
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages


object Banner {
  type Props = (pages.Router, String, String)

  val component = ScalaComponent.builder[Props]("Banner").
    render_P({ case (router, icon, title) =>
      <.h1(^.className := "banner",
        Image.component(router, icon),
        <.span(title)) }).
    build
}
