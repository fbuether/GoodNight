
package goodnight.stories

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
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {
    // def append(): Callback


    def loadScenes(props: Props): AsyncCallback[VdomElement] =
      Request(ApiV1.AvailableScenes, props.story.urlname).send.
        forStatus(200).
        forJson[List[model.Scene]].
        body.attemptTry.map({
          case Success(scenes) =>
            scenes.filter(_.mandatory).headOption match {
              case Some(scene) =>
                Scene.component(Scene.Props(props.router, scene, props.player))
              case None =>
                renderSelection(props, scenes)
            }
          case Failure(e) =>
            Error.component(e, false)
        })

    def renderSelection(props: Props, scenes: List[model.Scene]) = {
      <.div(
        scenes.map(scene =>
          <.div("scene: " + scene.title)).
          toTagMod
      )
    }

    def render(props: Props, state: State) =
      <.div(^.id := "matter",
        <.div(^.id := "centre",
          Loading.suspend(props.router, loadScenes(props))),
        <.div(^.id := "side",
          <.h3(props.player.name),
          "side infos."))
  }

  def component = ScalaComponent.builder[Props]("StoryRoll").
    initialState(State(())).
    renderBackend[Backend].
    build
}
