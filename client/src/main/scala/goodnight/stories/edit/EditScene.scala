
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import org.scalajs.dom.raw.HTMLElement
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.components.Editor
import goodnight.model
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object EditScene {
  type Props = (String)
  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    val overlayRef = Ref[HTMLElement]
    val enlargeRef = Ref[HTMLElement]
    val shrinkRef = Ref[HTMLElement]

    def toggleClass(elRef: Ref.Simple[HTMLElement], toggle: String) =
      elRef.foreach({ el =>
        el.className =
          if (el.className.contains(toggle))
            el.className.replace(toggle, "").trim()
          else
            el.className + " " + toggle
      })

    def toggleEnlarge =
      (toggleClass(overlayRef, "fullscreen") >>
        toggleClass(enlargeRef, "hidden") >>
        toggleClass(shrinkRef, "hidden"))

    val editorRef = Editor.componentRef

    def cancel = Callback({
      println("canceling.")
    })

    def save: Callback =
      bs.props.flatMap({ story =>
        editorRef.get.flatMapCB(_.backend.get).flatMap({ content =>
          Request(ApiV1.CreateScene, story).withBody(Json.obj(
            "text" -> content)).
            send.map({
              case Reply(201, _) =>
                println("great!")
              case e =>
                println("not so great: " + e)
            }).toCallback
        })
      })


    def render(props: Props) = {
      <.div.withRef(overlayRef)(^.className := "edit-scene overlay",
        <.h3("Add a new Scene"),
        <.a(^.className := "fullscreen-toggle",
          ^.onClick --> toggleEnlarge,
          <.i.withRef(enlargeRef)(
            ^.className := "fas fa-expand-arrows-alt",
            ^.title := "Switch to fullscreen view"),
          <.i.withRef(shrinkRef)(
            ^.className := "fas fa-compress-arrows-alt hidden",
            ^.title := "Return to regular display")),
        editorRef.component("editor"),
        <.div(^.className := "buttons",
          <.button(^.className := "inline",
            ^.onClick --> cancel,
            <.i(^.className := "fas fa-ban label"),
            "Cancel"),
          <.button(^.className := "inline",
            ^.onClick --> save,
            <.i(^.className := "far fa-check-square label"),
            "Save")))
    }
  }


  val component = ScalaComponent.builder[Props]("EditScene").
    initialState[State](()).
    renderBackend[Backend].
    build
}
