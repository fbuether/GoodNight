
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages


object Image {
  type Props = (pages.Router, String)

  val component = ScalaComponent.builder[Props]("Banner").
    render_P({ case (router, image) =>
      <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
        image).value)
    }).
    build
}
