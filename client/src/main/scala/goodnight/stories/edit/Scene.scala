
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
  case class Props(router: pages.Router, story: edit.Story,
    sceneUrlname: Option[String])
  case class State(scene: Option[edit.Scene], changed: Boolean,
    saving: Boolean, loading: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    def loadScene: Callback =
      bs.props.flatMap(props => props.sceneUrlname match {
          case None => bs.modState(_.copy(loading = false))
          case Some(sceneUrlname) =>
            Request(ApiV1.Scene, props.story.urlname, sceneUrlname).send.
              forStatus(200).forJson[edit.Scene].
              body.flatMap(scene =>
                bs.modState(_.copy(
                  scene = Some(scene),
                  loading = false)).async).toCallback
      })

    def cancel: Callback =
      bs.props.flatMap(props =>
        // todo: ask for confirmation if (state.changed)
        props.router.set(pages.EditStory(props.story.urlname)))

    def save: Callback =
      // todo: check for intermediate changes, e.g. with a revision number
      bs.modState(_.copy(saving = true)) >>
      bs.props.zip(bs.state).flatMap({ case (props, state) =>
        editorRef.get.flatMapCB(_.backend.get).flatMap({ rawText =>
          val request = state.scene match {
            case None => Request(ApiV1.CreateScene, props.story.urlname)
            case Some(scene) => Request(ApiV1.SaveScene, props.story.urlname,
              scene.urlname)
          }

          (request.withPlainBody(rawText).send.
            forStatus(202).forJson[edit.Scene].
            body.flatMap(scene =>
              bs.modState(_.copy(
                scene = Some(scene),
                saving = false,
                changed = false)).async).toCallback)
      })
    })

    val editorRef = Editor.componentRef
    def setChanged = bs.modState(_.copy(changed = true)).void


    def renderEditor(props: Props, state: State, scene: Option[edit.Scene]) = {
      val title = scene.map(_.name).getOrElse("A new Scene")
      val content = scene.map(_.raw).getOrElse("$ name: new scene")
      val canSave = !state.saving && state.changed

      <.div(
        <.h3("Writing: ", title),
        editorRef.component(Editor.Props(content, setChanged)),
        <.div(^.className := "as-columns for-buttons",
          <.div(
            <.button(
              ^.onClick --> cancel,
              <.i(^.className := "label " +
                (if (state.changed) "far fa-trash-alt"
                else "far fa-arrow-alt-circle-left")),
              (if (state.changed) "Discard" else "Return"))),
          <.div(
            SavingButton.render(canSave, state.saving, save)(
              if (state.saving) "Saving…" else "Save"))))
    }

    def render(props: Props, state: State): VdomElement =
      if (state.loading) Loading.component(props.router)
      else renderEditor(props, state, state.scene)
  }

  val component = ScalaComponent.builder[Props]("edit.Scene").
    initialStateFromProps(props =>
      // if we create a new scene, we can save immediately.
      (State(None, props.sceneUrlname.isEmpty, false, true))).
    renderBackend[Backend].
    componentDidMount(_.backend.loadScene).
    build


  def render(router: pages.Router, story: String, scene: Option[String]) =
    Shell.component(router)(
      WithEditStory.component(WithEditStory.Props(router, story,
        content => component(Props(router, content.story, scene)))))

  def renderEdit(page: pages.EditScene, router: pages.Router) =
    render(router, page.story, Some(page.scene))

  def renderAdd(page: pages.AddScene, router: pages.Router) =
    render(router, page.story, None)
}
