
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
    player: model.Player, firstScene: model.SceneView)
  case class State(scene: model.SceneView)

  class Backend(bs: BackendScope[Props, State]) {
    def doScene(next: String): Callback =
      bs.state.flatMap(state =>
        Request(ApiV1.Do, state.scene.story, next).send.
          forStatus(202).forJson[(model.Activity, model.SceneView)].
          body.flatMap({ case (activity, scene) =>
            bs.modState(_.copy(scene = scene)).async
          }).
          toCallback)

    def render(props: Props, state: State): VdomElement = {
      val inner: VdomElement =
        // state.scene match {
        // case None =>
        //   <.div(
        //     <.h2("Loading"),
        //     Loading.component(props.router))
        // case Some((story, choices)) =>
          Scene.component(Scene.Props(props.router, props.story,
            props.player, state.scene, doScene))
      // }

      <.div(^.id := "matter",
        <.div(^.id := "centre",
          inner),
        <.div(^.id := "side",
          <.h4("Sir Archibald")))
    }
  }

  val component = ScalaComponent.builder[Props]("Story").
    initialStateFromProps(props => State(props.firstScene)).
    renderBackend[Backend].
    // componentDidMount(_.backend.loadScene).
    // componentWillUpdate(bs => bs.backend.updateProps(bs)).
    build


  def withStory(router: pages.Router, storyData: WithStory.StoryData) =
    storyData match {
      case (story, None) =>
        CreatePlayer.component(CreatePlayer.Props(router, story,
          (playerData: CreatePlayer.PlayerState) =>
          component(Props(router, story, playerData._1, playerData._3))))
      case (story, Some(playerData)) =>
        component(Props(router, story, playerData._1, playerData._3))
    }

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story,
        withStory(router, _))))
}
