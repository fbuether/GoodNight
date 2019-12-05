
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
    player: model.Player)
  case class State(view: Option[VdomElement])

  class Backend(bs: BackendScope[Props, State]) {
    def loadInitialActivity: Callback =
      gotoLocation(None)

    def gotoLocation(location: Option[model.Location]): Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.AvailableScenes, props.story.urlname).send.
          forStatus(200).forJson[List[model.Scene]].
          body.flatMap(scenes =>
            setView(ReadLocation.component(ReadLocation.Props(
              props.router, props.player, location, scenes,
              gotoScene))).
              async).
          toCallback)

    def gotoScene(scene: model.Scene): Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.DoScene, props.story.urlname, scene.urlname).send.
          forStatus(200).forJson[(model.Scene, Seq[model.Choice])].
          body.flatMap(sceneChoices =>
            setView(ReadScene.component(ReadScene.Props(
              props.router, props.player, sceneChoices._1, sceneChoices._2,
              gotoChoice(sceneChoices._1, _)))).
              async).
          toCallback)

    def gotoChoice(scene: model.Scene, choice: model.Choice): Callback =
      bs.props.flatMap(props =>
        Request(ApiV1.DoChoice, props.story.urlname, scene.urlname,
          choice.title).send.
          forStatus(200).// forJson[model.Choice] what kind of results?
          body.flatMap(_ =>
            setView(ReadChoice.component(ReadChoice.Props(
              props.router, props.player, scene, choice,
              gotoLocation))).
              async).
          toCallback)


    def setView(newView: VdomElement): Callback =
      bs.setState(State(Some(newView)))

    def render(props: Props, state: State): VdomElement =
      state.view match {
        case None => Loading.component(props.router)
        case Some(component) => component
      }
  }

  val component = ScalaComponent.builder[Props]("Story").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadInitialActivity).
    // componentWillUpdate(bs => bs.backend.updateProps(bs)).
    build



  def withStory(router: pages.Router, storyData: WithStory.StoryData) =
    storyData match {
      case (story, None) =>
        CreatePlayer.component(CreatePlayer.Props(router, story, player =>
          component(Props(router, story, player))))
      case (story, Some(player)) => component(Props(router, story, player))
    }

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(
      WithStory.component(WithStory.Props(router, page.story,
        withStory(router, _))))
}
