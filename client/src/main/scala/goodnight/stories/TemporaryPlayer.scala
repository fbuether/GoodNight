
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


object TemporaryPlayer {
  // StoryData is the shape of the reply of ApiV1.CreatePlayer
  type PlayerState = CreatePlayer.PlayerState

  case class Props(router: pages.Router, story: model.Story,
    child: PlayerState => VdomElement)
  case class State(data: Option[PlayerState], saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {

    def createPlayer(storyUrlname: String): Callback =
      bs.modState(_.copy(saving = true)).>>(
        Request(ApiV1.CreateTemporary, storyUrlname).send.
          forStatus(201).forJson[PlayerState].
          body.flatMap(ps =>
            bs.modState(_.copy(saving = false, data = Some(ps))).async).
          toCallback)


    def renderForm(props: Props, saving: Boolean) =
      <.div(
        <.h2("Welcome!"),
        <.p("You are not signed into the GoodNight!"),
        <.p("No worries. You can either ",
          props.router.link(pages.SignInFor(
            props.router.pathFor(pages.Story(props.story.urlname)).value))(
            "sign in now"),
          " or ",
          props.router.link(pages.Register)(
            "register yourself"),
          "."),
        <.h3("Start playing"),
        <.div(^.className := "as-columns",
          <.div(
            <.p("If you don't want to register, you can still play!"),
            <.p("Every player needs a name. As a temporary player, you will ",
              "play ", <.strong(props.story.name), " as:"),
            <.div(^.className := "inset",
              <.em("Mrs. Hollywoockle")),
            SavingButton.render("center", "fas fa-angle-double-right",
              true, saving, createPlayer(props.story.urlname))("Start!")),
          <.div(^.className := "box",
            <.p("Please note that your progress will be ", <.strong("lost"),
              " when you quit playing. If you want to save your progress, ",
              "just register, and we will keep your game."))))

    def render(props: Props, state: State) = state match {
      case State(None, saving) => renderForm(props, saving)
      case State(Some(playerState), _) => props.child(playerState)
    }
  }

  val component = ScalaComponent.builder[Props]("CreatePlayer").
    initialState(State(None, false)).
    renderBackend[Backend].
    build
}
