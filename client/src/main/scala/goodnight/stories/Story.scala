
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillUpdate
import org.scalajs.dom.html

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.read
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Story {
  case class Props(router: pages.Router, story: read.Story,
    player: read.Player,
    state: read.States,
    firstActivity: read.Activity,
    firstScene: read.Scene)
  case class State(activity: read.Activity, scene: read.Scene,
    state: read.States)

  def mergeState(old: read.States, effects: read.States) =
    // todo: wow, this is inefficient.
    effects.foldLeft(old)((old,effect) =>
      // todo: remove this state if effect.value is 0 or false
      if (old.exists(_.quality == effect.quality))
        old.map(state =>
          if (state.quality == effect.quality) effect
          else state)
      else
        old :+ effect)

  class Backend(bs: BackendScope[Props, State]) {
    private val centreRef = Ref[html.Div]

    def goScene(next: String): Callback =
      (bs.state.flatMap(state =>
        Request(ApiV1.GoScene, state.scene.story, next).send.
          forStatus(202).forJson[read.Outcome].
          body.flatMap({ case (activity, scene) =>
            bs.modState(old => old.copy(
              activity = activity,
              scene = scene,
              state = mergeState(old.state, activity.effects))).async
          }).
          toCallback) >>
        centreRef.foreach(_.scrollIntoView(true)))

    def render(props: Props, state: State): VdomElement =
      <.div(^.id := "matter",
        <.div.withRef(centreRef)(^.id := "centre",
          Scene.component(Scene.Props(props.router, props.story,
            props.player, props.state, state.activity.effects,
            state.scene, goScene))),
        <.div(^.id := "side",
          <.h3(props.player.name),
          <.p("Your current state:"),
          Qualities.component(Qualities.Props(props.router, props.story,
            state.state)),
          <.hr(^.id := "subside")))
  }

  val component = ScalaComponent.builder[Props]("Story").
    initialStateFromProps(props => State(props.firstActivity,
      props.firstScene, props.state)).
    renderBackend[Backend].
    build

  def build(router: pages.Router, story: read.Story,
    ps: read.PlayerState) =
    component(Props(router, story, ps._1, ps._2, ps._3, ps._4))


  def withStory(router: pages.Router, storyData: read.StoryState) =
    storyData match {
      case (story, None) => AuthenticationService.getUser match {
        case Some(_) =>
          CreatePlayer.component(CreatePlayer.Props(router, story,
            playerState => build(router, story, playerState)))
        case None =>
          TemporaryPlayer.component(TemporaryPlayer.Props(router, story,
            playerState => build(router, story, playerState)))
      }
      case (story, Some(playerState)) =>
        build(router, story, playerState)
    }

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story, true,
        withStory(router, _))))
}
