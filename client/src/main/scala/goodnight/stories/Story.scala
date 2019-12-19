
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillUpdate

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Story {
  case class Props(router: pages.Router, story: model.Story,
    player: model.Player, firstSceneUrlname: String)
  case class State(sceneUrlname: String,
    scene: Option[(model.Scene, Seq[model.Scene])])

  class Backend(bs: BackendScope[Props, State]) {
    def loadScene: Callback =
      bs.props.zip(bs.state).flatMap(ps =>
        Request(ApiV1.Scene, ps._1.story.urlname, ps._2.sceneUrlname).send.
          forStatus(200).forJson[(model.Scene, Seq[model.Scene])].
          body.flatMap(scene =>
            bs.modState(_.copy(scene = Some(scene))).async).
          toCallback)

    def goto(next: model.Scene): Callback =
      Callback.log(s"going to $next")

    def render(props: Props, state: State): VdomElement = {
      val inner: VdomElement = state.scene match {
        case None =>
          <.div(
            <.h2("Loading"),
            Loading.component(props.router))
        case Some((story, choices)) =>
          Scene.component(Scene.Props(props.router, props.story,
            props.player, story, choices, goto))
      }

      <.div(^.id := "matter",
        <.div(^.id := "centre",
          inner),
        <.div(^.id := "side",
          <.h4("Sir Archibald")))
    }
  }

  val component = ScalaComponent.builder[Props]("Story").
    initialStateFromProps(props => State(props.firstSceneUrlname, None)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadScene).
    // componentWillUpdate(bs => bs.backend.updateProps(bs)).
    build


  def withStory(router: pages.Router, storyData: WithStory.StoryData) =
    storyData match {
      case (story, None) =>
        CreatePlayer.component(CreatePlayer.Props(router, story,
          (playerData: CreatePlayer.PlayerState) =>
          component(Props(router, story, playerData._1, playerData._2.scene))))
      case (story, Some(playerData)) =>
        component(Props(router, story, playerData._1, playerData._2.scene))
    }

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story,
        withStory(router, _))))
}
