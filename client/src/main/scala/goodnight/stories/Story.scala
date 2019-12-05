
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

  // case class State(
  //   n: Unit
  // //   story: Option[model.Story],
  // //   player: Option[model.Player],
  // //   location: Option[model.Location],
  // //   scene: Option[model.Scene],
  // //   choice: Option[model.Choice]
  // )

  // class Backend(bs: BackendScope[Props, State]) {
  //   // def loadStory(props: Props): AsyncCallback[Unit] =
  //   //   Request(ApiV1.Story, props.storyUrlname).send.
  //   //     forStatus(200).forJson[(model.Story, Option[model.Player])].
  //   //     body.flatMap({ case (story, playerOpt) =>
  //   //       bs.modState(_.copy(story = Some(story),
  //   //         player = playerOpt)).async
  //   //     })


  //   // def loadState =
  //   //   bs.props.flatMap({ props =>
  //   //     loadStory(props).toCallback
  //   //   })

  //   // def updateProps(bs: ComponentWillUpdate[Props, State, Backend]) = {
  //   //   Callback.log("*** Story: did update props.")
  //   // }

  //   // def saveNewPlayer(playerName: String): Callback =
  //   //   bs.state.flatMap({ state =>
  //   //     Request(ApiV1.CreatePlayer, state.story.get.urlname).
  //   //       withBody(ujson.Obj("name" -> playerName)).send.
  //   //       forStatus(201).
  //   //       forJson[model.Player].
  //   //       completeWith({
  //   //         case Failure(e) =>
  //   //           Callback.log("oh my, an error: " + e)
  //   //         case Success(Reply(_, player)) =>
  //   //           bs.modState(_.copy(player = Some(player)))
  //   //       })
  //   //   })

  //   // def renderLoading(router: pages.Router) =
  //   //   <.div(
  //   //     Banner.component(router, "Alien World.png", "Loading story..."),
  //   //     Loading.component(router))

  //   // def renderCreatePlayer(router: pages.Router, story: model.Story) =
  //   //   <.div(
  //   //     Banner.component(router, story.image, story.name),
  //   //     CreatePlayer.component(CreatePlayer.Props(router, story,
  //   //       AuthenticationService.getUser.get,
  //   //       saveNewPlayer))
  //   //   )

  //   // def renderStory(router: pages.Router, story: model.Story,
  //   //   player: model.Player) =
  //   //   <.div(
  //   //     Banner.component(router, story.image, story.name)// ,
  //   //     // StoryRoll.component(StoryRoll.Props(router, story,
  //   //     //   player, None))
  //   //   )

  //   // def render(props: Props, state: State): VdomElement =
  //   //   (state.story, state.player) match {
  //   //     case (None, _) => renderLoading(props.router)
  //   //     case (Some(story), None) => renderCreatePlayer(props.router, story)
  //   //     case (Some(story), Some(player)) =>
  //   //       renderStory(props.router, story, player)
  //   //   }
  // }

  // val component = ScalaComponent.builder[Props]("Story").
  //   initialState(State(None, None, None, None, None)).
  //   renderBackend[Backend].
  //   // componentDidMount(_.backend.loadState).
  //   // componentWillUpdate(bs => bs.backend.updateProps(bs)).
  //   build


  val component = ScalaComponent.builder[Props]("Story").
    render_P(props =>
      <.div(
        "reading story with",
        props.story.name,
        " and ",
        props.player.name,
        "!")
    ).
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
