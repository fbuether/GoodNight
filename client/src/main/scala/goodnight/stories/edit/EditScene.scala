
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
  type Props = (pages.Router, model.Story, Option[model.Scene])
  // stores if the scene has been changed since we mounted.
  type State = (Boolean)

  class Backend(bs: BackendScope[Props, State]) {

    def cancel = bs.props.zip(bs.state).flatMap({ (props, state) =>
      val (router, story, _) = props
      val changed = state
      // todo: ask for confirmation if (changed)
      router.set(pages.EditStory(story.urlname))
    })

    def save: Callback =
      bs.props.zip(bs.state).flatMap({ (props, state) =>
        val (router, story, scene) = props
        val changed = state
        val isNew = story.isEmpty

        editorRef.get.flatMapCB(_.backend.get).flatMap({ raw =>

          val target =
            if (isNew) Request(ApiV1.CreateScene, story.urlname)
            else Request(ApiV1.EditScene, story.urlname, scene.urlname)

          target.
            withBody(Json.obj("text" -> content)).
            send.map({
              case Reply(201, _


          Request(ApiV1.Create


        .flatMap({ content =>
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

    def render(props: Props) = {
      val (router, story, scene) = props
      val isNew = scene.isEmpty

      val title = if (isNew) "Write a new Scene" else "Edit: " + scene.get.title
      var content = if (isNew) "# New Scene" else scene.get.raw

      <.div.withRef(overlayRef)(^.className := "edit-scene overlay",
        <.h3(title),
        <.a(^.className := "fullscreen-toggle",
          ^.onClick --> toggleEnlarge,
          <.i.withRef(enlargeRef)(
            ^.className := "fas fa-expand-arrows-alt",
            ^.title := "Switch to fullscreen view"),
          <.i.withRef(shrinkRef)(
            ^.className := "fas fa-compress-arrows-alt hidden",
            ^.title := "Return to regular display")),
        editorRef.component(content),
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
    initialState[State](false).
    renderBackend[Backend].
    build
}
