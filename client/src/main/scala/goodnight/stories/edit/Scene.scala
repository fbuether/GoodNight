
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.edit
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }
import goodnight.stories.WithStory


object Scene {
  case class Props(router: pages.Router,
    story: edit.Story,
    scene: edit.Scene,

    change: Callback,
    save: String => Callback,

    saving: Boolean,
    changed: Boolean,
    error: Option[String])


  class Backend(bs: BackendScope[Props, Unit]) {
    def cancel: Callback =
      bs.props.flatMap(props =>
        // todo: ask for confirmation if (state.changed)
        props.router.set(pages.EditStory(props.story.urlname)))

    val editorRef = Editor.componentRef
    def doSave: Callback =
      editorRef.get.flatMapCB(_.backend.get).flatMap(text =>
        bs.props.flatMap(props =>
          props.save(text)))


    def renderReferences(router: pages.Router, scene: edit.Scene) =
      <.div(^.className := "ref-lists",
        <.h4("References"),
        <.p("This scene is referred in:"),
        (if (scene.prevs.length > 0)
          <.ul(
            scene.prevs.map(prev =>
              <.li(
                router.link(pages.EditScene(scene.story, prev))(prev))).
              toTagMod)
        else
          <.p("Nowhere.")),
        <.p("This scene refers:"),
        (if (scene.prevs.length > 0)
          <.ul(
            scene.nexts.map({ case (next, exists) =>
              <.li(
                router.link(
                  (if (exists) pages.EditScene(scene.story, next)
                  else pages.AddSceneNamed(scene.story, next)))(
                  next)) }).
              toTagMod)
        else
          <.p("Nothing.")))


    def render(props: Props): VdomElement = {
      val canSave = !props.saving && props.changed

      <.div(
        <.h3("Writing: ", props.scene.name),
        <.div(^.className := "as-columns",
          <.div(^.className := "three-columns",
            editorRef.component(Editor.Props(props.scene.raw, props.change))),
          renderReferences(props.router, props.scene),
        ),
        props.error.map(err =>
          TagMod(<.p(^.className := "error",
            "An error occurred when trying to save: ",
            err),
            <.p("The scene has ", <.strong("not"), " been saved."))
        ).getOrElse(""),
        <.div(^.className := "as-columns for-buttons",
          <.div(
            <.button(
              ^.onClick --> cancel,
              <.i(^.className := "label " +
                (if (props.changed) "far fa-trash-alt"
                else "far fa-arrow-alt-circle-left")),
              (if (props.changed) "Discard" else "Return"))),
          <.div(
            SavingButton.render(canSave, props.saving, doSave)(
              if (props.saving) "Saving…" else "Save"))))
    }
  }

  val component = ScalaComponent.builder[Props]("edit.Scene").
    stateless.
    renderBackend[Backend].
    build
}
