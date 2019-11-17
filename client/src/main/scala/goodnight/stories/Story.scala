
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


object Story {
  case class Props(router: pages.Router, storyUrlname: String)

  case class State(
    story: Option[model.Story],
    player: Option[model.Player],
    loading: Boolean
  )

  class Backend(bs: BackendScope[Props, State]) {
    def loadState =
      bs.props.flatMap({ props =>
        Request(ApiV1.Story, props.storyUrlname).send.
          forStatus(200).forJson[(model.Story, Option[model.Player])].
          body.completeWith({
            case Success((story, playerOpt)) =>
              bs.modState(_.copy(story = Some(story),
                player = playerOpt,
                loading = false))
            case Failure(e) =>
              Callback.log("well, something's wrong." + e)
          })
      })

    def saveNewPlayer(playerName: String): Callback =
      bs.state.flatMap({ state =>
        Request(ApiV1.CreatePlayer, state.story.get.urlname).
          withBody(ujson.Obj("name" -> playerName)).send.
          forStatus(201).
          forJson[model.Player].
          completeWith({
            case Failure(e) =>
              Callback.log("oh my, an error: " + e)
            case Success(Reply(_, player)) =>
              bs.modState(_.copy(player = Some(player)))
          })
      })

    def render(props: Props, state: State): VdomElement = state match {
      case State(_, _, true) =>
        <.div(
          Banner.component(props.router, "Alien World.png", "Loading story..."),
          Loading.component(props.router))
      case State(Some(story), None, _) =>
        <.div(
          Banner.component(props.router, story.image, story.name),
          CreatePlayer.component(CreatePlayer.Props(props.router, story,
            AuthenticationService.getUser.get,
            saveNewPlayer)))
      case State(Some(story), Some(player), _) =>
        <.div(
          Banner.component(props.router, story.image, story.name),
          StoryRoll.component(StoryRoll.Props(props.router, story,
            player)))
      case _ =>
        Error.component(new Error("somethings wrong."), false)
    }
  }

  val component = ScalaComponent.builder[Props]("ReadStory").
    initialState(State(None, None, true)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadState).
    build

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(this.component(
      Props(router, page.story)))
}
