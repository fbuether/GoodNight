
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html

// inspiration:
// https://github.com/amirkarimi/neptune/blob/master/src/main/scala/com/..
// github/neptune/Main.scala

object Editor {
  type Props = String
  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    val contentRef = Ref[html.Div]

    def execute(e: ReactEventFromInput): Callback = Callback({
      print("action: " + e)
    })

    def get: CallbackTo[String] = contentRef.get.
      map(_.innerHTML).getOrElse("")

    def render(props: Props, state: State) = {
      <.div(^.className := "editor",
        <.div(^.className := "tools",
          <.button("undo",
            ^.onClick ==> execute,
            ^.title := "Undo the previous editing step"),
          <.button("redo",
            ^.onClick ==> execute,
            ^.title := "Redo the previously undone step"),
          <.button("B",
            ^.onClick ==> execute,
            ^.title := "Make selected text bold"),
          <.button("I",
            ^.onClick ==> execute,
            ^.title := "Make selected text italic"),
          <.button("H1",
            ^.onClick ==> execute,
            ^.title := "Turn selected paragraph into a heading"),
          <.button("p",
            ^.onClick ==> execute,
            ^.title := "Turn selected paragraph into a regular paragraph"),
          <.button("li",
            ^.onClick ==> execute,
            ^.title := "Turn selected paragraph into a list"),
        ),
        <.div.withRef(contentRef)(^.className := "content",
          ^.contentEditable := "true",
            "This is a bit of content that, well, is editable."
        ))
    }
  }

  val component = ScalaComponent.builder[Props]("Editor").
    initialState[State](()).
    renderBackend[Backend].
    build

  def componentRef = Ref.toScalaComponent(component)
}
