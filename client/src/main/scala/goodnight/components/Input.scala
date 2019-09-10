
package goodnight.components

import org.scalajs.dom.html
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


object Input {
  case class Props(
    label: String,
    name: String,
    props: Seq[TagMod] = Seq(),
    // defaultValue: Option[String] = None,
    // required: Bool = true,
    password: Boolean = false
  )

  case class State(
  )

  class Backend(bs: BackendScope[Props, State]) {
    val inputRef = Ref[html.Input]

    def render(p: Props, s: State): VdomElement = {
      val ty = if (p.password) <.input.password else <.input.password
      <.label(^.className := "captioned",
        p.label,
        ty(^.name := p.name,
          p.props.toTagMod,
          //   p.defaultValue.whenDefined(d => TagMod(^.defaultValue := d)),
          //   ^.required := p.required,
        ).withRef(inputRef))
    }
  }

  def component = ScalaComponent.builder[Props]("Input").
    initialState(State()).
    renderBackend[Backend].
    build
}
