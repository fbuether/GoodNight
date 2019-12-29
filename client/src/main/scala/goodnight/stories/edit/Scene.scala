
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
    sceneUrlname: String)
  case class State(scene: Option[model.Scene])

  class Backend(bs: BackendScope[Props, State]) {
    def loadScene: Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.Scene, props.story.urlname, props.sceneUrlname).send.
          forStatus(200).forJson[model.Scene].
          body.flatMap(scene =>
            bs.modState(_.copy(scene = Some(scene))).async).
          toCallback)

    def render(props: Props, state: State): VdomElement = state.scene match {
      case None => Loading.component(props.router)
      case Some(scene) =>
        <.div(
          scene.text)
    }
  }


  val component = ScalaComponent.builder[Props]("edit.Story").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadScene).
    build


  def withStory(router: pages.Router, storyData: WithStory.StoryData,
    scene: String) =
    component(Props(router, storyData._1, scene))

  def render(page: pages.EditScene, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story, false,
        withStory(router, _, page.scene))))
}
