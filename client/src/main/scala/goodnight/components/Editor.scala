
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.Attr.Generic
import org.scalajs.dom.window
import org.scalajs.dom.html
import scala.math.min
import scala.math.floor

// inspiration:
// https://github.com/amirkarimi/neptune/blob/master/src/main/scala/com/..
// github/neptune/Main.scala
// -- not anymore, defaulted back to textarea.

object Editor {
  case class Props(content: String, onFirstChange: Callback = Callback.empty)
  case class State(sentFirstChange: Boolean)


  // val suppressContentEditableWarning =
  //   new Generic("suppressContentEditableWarning")

  class Backend(bs: BackendScope[Props, State]) {
    val contentRef = Ref[html.TextArea]

    // def execute(e: ReactEventFromInput): Callback = Callback({
    //   document.execCommand("bold")
    //   print("actioned: " + e)
    // })

    def get: CallbackTo[String] = contentRef.get.
      map(_.value).getOrElse("")

    // https://stackoverflow.com/a/25621277
    def fitTextarea: Callback =
      contentRef.foreach({ ta =>
        ta.style.height = "auto"
        val targetHeight = min(ta.scrollHeight, floor(window.innerHeight * 0.6))
        ta.style.height = targetHeight + "px"
      })

    def onFirstChange: Callback =
      bs.state.flatMap({ state =>
        if (state.sentFirstChange) Callback.empty
        else (bs.modState(_.copy(sentFirstChange = true)) >>
          bs.props.flatMap(_.onFirstChange))
      })

    def render(props: Props, state: State) = {
      <.div(^.className := "editor",
        // <.div(^.className := "tools",
        //   <.button("undo",
        //     ^.onClick ==> execute,
        //     ^.title := "Undo the previous editing step"),
        //   <.button("redo",
        //     ^.onClick ==> execute,
        //     ^.title := "Redo the previously undone step"),
        //   <.button("B",
        //     ^.onClick ==> execute,
        //     ^.title := "Make selected text bold"),
        //   <.button("I",
        //     ^.onClick ==> execute,
        //     ^.title := "Make selected text italic"),
        //   <.button("H1",
        //     ^.onClick ==> execute,
        //     ^.title := "Turn selected paragraph into a heading"),
        //   <.button("p",
        //     ^.onClick ==> execute,
        //     ^.title := "Turn selected paragraph into a regular paragraph"),
        //   <.button("li",
        //     ^.onClick ==> execute,
        //     ^.title := "Turn selected paragraph into a list"),
        // ),
        <.textarea.withRef(contentRef)(^.className := "content",
          ^.spellCheck := true,
          ^.acceptCharset := "UTF-8",
          // ^.contentEditable := "true",
          // suppressContentEditableWarning := "true",
          ^.onInput --> (onFirstChange >> fitTextarea),
          ^.defaultValue := props.content
        ))

        // <.div.withRef(contentRef)(^.className := "content",
        //   ^.contentEditable := "true",
        //   suppressContentEditableWarning := "true",
        //   ^.onInput --> onFirstChange,
        //   props.content
        // ))
    }
  }

  val component = ScalaComponent.builder[Props]("Editor").
    initialState[State](State(false)).
    renderBackend[Backend].
    componentDidMount(_.backend.fitTextarea).
    build

  def componentRef = Ref.toScalaComponent(component)
}
