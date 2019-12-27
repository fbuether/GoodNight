
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages


object Markdown {
  type Props = String

  val component = ScalaComponent.builder[Props]("Markdown").
    render_PC({case (text: Props, children: PropsChildren) =>
      // todo: actual markdown parsing and all that.
      <.p(
        text.split("\n").
          foldRight(TagMod())((a,bl) => TagMod(a, <.br(), bl)),
        children)
    }).
    build
}
