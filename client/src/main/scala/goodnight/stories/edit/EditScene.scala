
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import org.scalajs.dom.raw.HTMLElement
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.components.Editor
import goodnight.model
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object EditScene {
  case class Props(router: pages.Router, story: model.Story,
    scene: Option[model.Scene], onSave: String => AsyncCallback[Unit])

  case class State(changed: Boolean, saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {

    def cancel = bs.props.zip(bs.state).flatMap({ case (props, state) =>
      // todo: ask for confirmation if (state.changed)
      props.router.set(pages.EditStory(props.story.urlname))
    })

    def save =
      bs.modState(_.copy(saving = true)) >>
      bs.props.zip(bs.state).flatMap({ case (props, state) =>
      editorRef.get.flatMapCB(_.backend.get).flatMap({ rawText =>
        (props.onSave(rawText) >>
          bs.modState(_.copy(saving = false, changed = false)).async).
          toCallback
      })
    })

    def setChanged =
      bs.modState(_.copy(changed = true)).void

    val editorRef = Editor.componentRef

    // resizing of overall scene editor.
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

    def render(props: Props, state: State) = {
      val isNew = props.scene.isEmpty
      val title =
        if (isNew) "Write a new Scene"
        else "Edit: " + props.scene.get.title
      var content = if (isNew) "# New Scene" else props.scene.get.raw

      <.div.withRef(overlayRef)(^.className := "edit-scene overlay",
        <.h3(title),
        <.a(^.className := "fullscreen-toggle",
          ^.onClick --> toggleEnlarge,
          <.i.withRef(enlargeRef)(
            ^.className := "fas fa-expand-arrows-alt",
            ^.title := "Switch to fullscreen view"),
          <.i.withRef(shrinkRef)(
            ^.className := "fas fa-compress-arrows-alt hidden",
            ^.title := "Return to regular view")),
        editorRef.component(Editor.Props(content, setChanged)),
        <.div(^.className := "buttons",
          <.button(^.className := "inline",
            ^.onClick --> cancel,
            <.i(^.className := "fas fa-ban label"),
            "Cancel"),
          <.button(^.className := "inline",
            ^.onClick --> save,
            (^.className := "loading").when(state.saving),
            (^.disabled := true).when(state.saving),
            <.i(
              (^.className := "far fa-spin fa-compass label").
                when(state.saving),
              (^.className := "fa fa-check-square label").when(!state.saving)),
            "Save")))
      }
  }


  val component = ScalaComponent.builder[Props]("EditScene").
    initialState[State](State(false, false)).
    renderBackend[Backend].
    build
}
