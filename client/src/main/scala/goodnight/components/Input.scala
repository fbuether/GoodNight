
package goodnight.components

import org.scalajs.dom.html
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


object Input {
  case class Props(
    label: String,
    name: String,
    props: Seq[TagMod] = Seq(),
    password: Boolean = false
  )

  case class State(
    value: String
  )

  class Backend(bs: BackendScope[Props, State]) {
    val inputRef = Ref[html.Input]

    def get: CallbackOption[String] =
      inputRef.get.map(_.value)

    def render(p: Props, s: State): VdomElement = {
      val ty = if (p.password) <.input.password else <.input.text
      ty(^.name := p.name,
        p.props.toTagMod,
        ^.placeholder := p.label).withRef(inputRef)
    }
  }

  val component = ScalaComponent.builder[Props]("Input").
    initialState(State("")).
    renderBackend[Backend].
    build


  type InputRef = Ref.WithScalaComponent[Props, State, Backend, CtorType.Props]

  def componentRef: InputRef = Ref.toScalaComponent(component)

  def withValue[R](input: InputRef, cont: String => CallbackTo[R]):
      CallbackTo[R] =
    // todo: don't just getOrElse("") a missing input. something's wrong in
    // that case, i guess.
    input.get.flatMap(_.backend.get).getOrElse("").flatMap(cont)
}
