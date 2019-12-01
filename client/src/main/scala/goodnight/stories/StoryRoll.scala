
package goodnight.stories

import java.util.UUID
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object StoryRoll {
  case class Props(router: pages.Router, story: model.Story,
    player: model.Player)
  case class State(action: Option[model.Action])

  class Backend(bs: BackendScope[Props, State]) {

    // activities
    def doLocation(location: Option[model.Location], scenes: Seq[model.Scene]) =
      Callback.log("switching to: showing scenes in location.") >>
      bs.modState(_.copy(action = None))

    def doScene(scene: model.Scene): Callback =
      Callback.log("switching to: entering " + scene.title) >>
      bs.modState(_.copy(action = Some(model.Action.Scene(scene))))


    def doChoice(choice: model.Choice): Callback =
      Callback.log("switching to: choosing " + choice.scene + "/" + choice.pos) >>
      bs.modState(_.copy(action = Some(model.Action.Choice(choice))))






    def renderNextAction(props: Props, state: State):
        AsyncCallback[VdomElement] =
      state.action match {
        case None =>
          // if we don't know what to do next, present everything possible
          // at this location.
          LocationAction.loadLocationAction(props.router, props.story,
            props.player, props.player.location, doScene)
        case Some(model.Action.Scene(scene)) =>
          SceneAction.doSceneAction(props.router, props.story,
            props.player, scene, doChoice)
        case Some(model.Action.Choice(choiceId)) =>
          renderChoice(props)
      }


    def renderScene(props: Props): AsyncCallback[VdomElement] =
      AsyncCallback.pure(<.div("hello, scene."))

    def renderChoice(props: Props): AsyncCallback[VdomElement] =
      AsyncCallback.pure(<.div("hello, choice."))


    def render(props: Props, state: State) =
      <.div(^.id := "matter",
        <.div(^.id := "centre",
          // StoryBacklog.component(StoryBacklog.Props(props.router,
          //   props.story,
          //   props.player, Seq())),


          Loading.suspend(props.router, renderNextAction(props, state))),
        <.div(^.id := "side",
          <.h3(props.player.name),
          "side infos."))
  }

  def component = ScalaComponent.builder[Props]("StoryRoll").
    initialState(State(None)).
    renderBackend[Backend].
    build
}
