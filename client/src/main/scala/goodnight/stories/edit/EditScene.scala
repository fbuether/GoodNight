
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
  type Props = Unit
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

    def render(props: Props) = {
      <.div.withRef(overlayRef)(^.className := "edit-scene overlay",
        <.h3("Edit Scene: whichever."),
        <.a(^.className := "fullscreen-toggle",
          ^.onClick --> toggleEnlarge,
          <.i.withRef(enlargeRef)(
            ^.className := "fas fa-expand-arrows-alt",
            ^.title := "Switch to fullscreen view"),
          <.i.withRef(shrinkRef)(
            ^.className := "fas fa-compress-arrows-alt hidden",
            ^.title := "Return to regular display")),
        Editor.component(),
        <.div(^.className := "buttons",
          <.button(^.className := "inline",
            <.i(^.className := "fas fa-ban label"),
            "Cancel"),
          <.button(^.className := "inline",
            <.i(^.className := "far fa-check-square label"),
            "Save")))
    }
  }


  val component = ScalaComponent.builder[Props]("EditScene").
    initialState[State](()).
    renderBackend[Backend].
    build
}
