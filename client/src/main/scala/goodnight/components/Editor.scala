
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

// inspiration:
// https://github.com/amirkarimi/neptune/blob/master/src/main/scala/com/..
// github/neptune/Main.scala

object Editor {
  type Props = Unit
  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    def execute(e: ReactEventFromInput): Callback = Callback({
      print("action: " + e)
    })

    def render(props: Props, state: State) = {
      <.div(^.className := "editor",
        <.div(^.className := "tools",
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
        <.div(^.className := "content",
          ^.contentEditable := "true",
            "This is a bit of content that, well, is editable."
        ))
    }
  }

  val component = ScalaComponent.builder[Props]("Editor").
    initialState[State](()).
    renderBackend[Backend].
    build
}
