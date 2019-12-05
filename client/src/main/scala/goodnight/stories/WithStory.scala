
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Request
import goodnight.service.Conversions._


object WithStory {
  type StoryData = (model.Story, Option[model.Player])

  case class Props(router: pages.Router,
    storyUrlname: String, child: StoryData => VdomElement)
  case class State(story: Option[StoryData])

  class Backend(bs: BackendScope[Props, State]) {
    def loadIfRequired(storyUrlname: String): Callback =
      Request(ApiV1.Story, storyUrlname).send.
        forStatus(200).
        forJson[StoryData].
        body.
        completeWith(result =>
          bs.setState(State(Some(result.get))))

    def render(props: Props, state: State): VdomElement = {
      println(s"rendering withStory: $props, $state")
      state.story match {
        case None => <.div(
          Banner.component(props.router, "Alien World.png", "Loading story..."),
          Loading.component(props.router))
        case Some(storyData) => <.div(
          Banner.component(props.router, storyData._1.image, storyData._1.name),
          props.child(storyData))
      }
    }
  }

  val component = ScalaComponent.builder[Props]("CreatePlayer").
    initialState(State(None)).
    renderBackend[Backend].
    componentDidMount(bs => bs.backend.loadIfRequired(bs.props.storyUrlname)).
    build

}
