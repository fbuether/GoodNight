
package goodnight.stories.edit

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }
import goodnight.stories.WithStory


object Scene {
  case class Props(router: pages.Router, story: model.Story,
    sceneUrlname: Option[String])
  case class State(scene: Option[model.Scene], changed: Boolean,
    saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    def loadScene: Callback =
      bs.props.flatMap(props => props.sceneUrlname match {
          case None => Callback.empty
          case Some(sceneUrlname) =>
            Request(ApiV1.Scene, props.story.urlname, sceneUrlname).send.
              forStatus(200).forJson[model.Scene].
              body.flatMap(scene =>
                bs.modState(_.copy(scene = Some(scene))).async).
              toCallback
        })

    def cancel: Callback =
      bs.props.flatMap(props =>
        // todo: ask for confirmation if (state.changed)
        props.router.set(pages.EditStory(props.story.urlname)))

    def save: Callback =
      bs.modState(_.copy(saving = true)) >>
      bs.props.zip(bs.state).flatMap({ case (props, state) =>
        editorRef.get.flatMapCB(_.backend.get).flatMap({ rawText =>
          val request = props.sceneUrlname match {
            case None => Request(ApiV1.CreateScene, props.story.urlname)
            case Some(scene) => Request(ApiV1.SaveScene, props.story.urlname,
              scene)
          }

          (request.withBody(rawText).send.forStatus(202) >>
            bs.modState(_.copy(saving = false, changed = false)).async).
            toCallback
      })
    })


    def setChanged =
      bs.modState(_.copy(changed = true)).void

    val editorRef = Editor.componentRef

    val newScene = ("Write a new Scene", "# New Scene")
    def ofScene(scene: model.Scene) = ("Edit: " + scene.name, scene.raw)

    def renderEditor(props: Props, state: State, text: (String, String)) = {
      val (title, content) = text

      <.div(
        <.h3(title),
        editorRef.component(Editor.Props(content, setChanged)),
        <.div(^.className := "buttons",
          <.button(^.className := "inline",
            ^.onClick --> cancel,
            <.i(^.className := "fas fa-ban label"),
            "Return / Discard"),
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

    def render(props: Props, state: State): VdomElement =
      (props.sceneUrlname, state.scene) match {
        case (None, None) => renderEditor(props, state, newScene)
        case (Some(_), Some(scene)) =>
          renderEditor(props, state, ofScene(scene))
        case _ => Loading.component(props.router)
      }
  }


  val component = ScalaComponent.builder[Props]("edit.Story").
    initialState(State(None, false, false)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadScene).
    build


  def render(router: pages.Router, story: String, scene: Option[String]) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, story, false, storyData =>
        component(Props(router, storyData._1, scene)))))

  def renderEdit(page: pages.EditScene, router: pages.Router) =
    render(router, page.story, Some(page.scene))

  def renderAdd(page: pages.AddScene, router: pages.Router) =
    render(router, page.story, None)
}
