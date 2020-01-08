
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillUpdate

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.play
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Story {
  case class Props(router: pages.Router, story: play.Story,
    player: play.Player,
    state: play.States,
    activity: play.Activity,
    firstScene: play.Scene)
  case class State(scene: play.Scene)

  class Backend(bs: BackendScope[Props, State]) {
    def doScene(next: String): Callback =
      bs.state.flatMap(state =>
        Request(ApiV1.DoScene, state.scene.story, next).send.
          forStatus(202).//forJson[(play.Activity, play.Scene)].
          body. // flatMap({ case (activity, scene) =>
            // bs.modState(_.copy(scene = scene)).async
          // }).
          toCallback)

    def render(props: Props, state: State): VdomElement =
      <.div(^.id := "matter",
        <.div(^.id := "centre",
          Scene.component(Scene.Props(props.router, props.story,
            props.player, props.state, state.scene, doScene))),
        <.div(^.id := "side",
          <.h3(props.player.name),
          Qualities.component(Qualities.Props(props.router, props.story,
            props.state))))
  }

  val component = ScalaComponent.builder[Props]("Story").
    initialStateFromProps(props => State(props.firstScene)).
    renderBackend[Backend].
    build


  def withStory(router: pages.Router, storyData: play.StoryState) =
    storyData match {
      case (story, None) => AuthenticationService.getUser match {
        case Some(_) =>
          CreatePlayer.component(CreatePlayer.Props(router, story,
            (playerData: CreatePlayer.PlayerState) =>
            component(Props(router, story, playerData._1, playerData._3))))
        case None =>
          TemporaryPlayer.component(TemporaryPlayer.Props(router, story,
            (playerData: CreatePlayer.PlayerState) =>
            component(Props(router, story, playerData._1, playerData._3))))
      }
      case (story, Some(pd)) =>
        component(Props(router, story,
          pd._1, pd._2, pd._3, pd._4))
    }

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story, true,
        withStory(router, _))))
}
