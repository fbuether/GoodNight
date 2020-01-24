
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


object SceneWrap {
  case class Props(router: pages.Router,
    story: edit.Story,
    scene: Option[String],
    isNew: Boolean)

  case class State(
    scene: Option[edit.Scene],
    saving: Boolean,
    changed: Boolean,
    error: Option[String])


  class Backend(bs: BackendScope[Props, State]) {
    def load: Callback =
      bs.props.flatMap(props =>
        if (props.isNew)
          bs.modState(_.copy(scene = Some(edit.Scene(
            props.story.urlname,
            props.scene.getOrElse("New Scene"),
            props.scene.getOrElse("New Scene"),
            "$ name: " + (props.scene.getOrElse("New Scene")),
            Seq(), Seq()))))
        else
          Request(ApiV1.Scene, props.story.urlname, props.scene.get).send.
            forStatus(200).forJson[edit.Scene].body.
            flatMap(scene =>
              bs.modState(_.copy(scene = Some(scene))).async).toCallback)

    def onChange: Callback =
      bs.modState(_.copy(changed = true))

    def onSave(raw: String): Callback =
      (bs.modState(_.copy(saving = true)) >>
      bs.props.flatMap({ props =>
        val request =
          if (props.isNew)
            Request(ApiV1.CreateScene, props.story.urlname)
          else
            Request(ApiV1.SaveScene, props.story.urlname, props.scene.get)

        request.withPlainBody(raw).send.flatMap({
          case Reply(202, scene) =>
            val editScene = read[edit.Scene](scene)
            if (props.isNew)
              props.router.set(pages.EditScene(props.story.urlname,
                editScene.urlname)).async
            else
              bs.modState(_.copy(
                scene = Some(editScene),
                saving = false,
                changed = false,
                error = None)).async
            case Reply(_, err) =>
              bs.modState(_.copy(
                saving = false,
                changed = true,
                error = Some(err))).async
          }).toCallback
      }))


    def render(props: Props, state: State) = state.scene match {
      case Some(scene) =>
        Scene.component(Scene.Props(
          props.router, props.story, scene,
          onChange, onSave,
          state.saving, state.changed || props.isNew, state.error))
      case None =>
        Loading.component(props.router)
    }
  }


  val component = ScalaComponent.builder[Props]("edit.SceneWrap").
    initialState(State(None, false, false, None)).
    renderBackend[Backend].
    componentDidMount(_.backend.load).
    build


  def render(router: pages.Router, story: String, scene: Option[String],
    isNew: Boolean) =
    Shell.component(router)(
      WithEditStory.component(WithEditStory.Props(router, story,
        content => component.withKey(scene.getOrElse("new scene") + isNew) (
          Props(router, content.story, scene, isNew)))))

  def renderEdit(page: pages.EditScene, router: pages.Router) =
    render(router, page.story, Some(page.scene), false)

  def renderAdd(page: pages.AddScene, router: pages.Router) =
    render(router, page.story, None, true)

  def renderAddNamed(page: pages.AddSceneNamed, router: pages.Router) =
    render(router, page.story, Some(page.name), true)
}
