
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.read
import goodnight.service.Request
import goodnight.service.Conversions._


object TemporaryPlayer {
  case class Props(router: pages.Router, story: read.Story,
    child: read.PlayerState => VdomElement)
  case class State(data: Option[read.PlayerState], saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    def createPlayer(storyUrlname: String): Callback =
      bs.modState(_.copy(saving = true)).>>(
        Request(ApiV1.CreateTemporary, storyUrlname).send.
          forStatus(201).forJson[read.PlayerState].
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
            <.p("But: You can also start playing right away!"),
            <.p("Every player needs a name. As a temporary player, you will ",
              "play \"", <.strong(props.story.name), "\" as:"),
            <.div(^.className := "inset",
              <.em("Mrs. Hollywoockle")),
            SavingButton.render("center", "fas fa-angle-double-right",
              true, saving, createPlayer(props.story.urlname))(
              if (saving) "Starting…" else "Start!")),
          <.div(^.className := "box",
            <.p("Please note that, as a temporary player, your progress will ",
              "be ", <.strong("lost"),
              " when you quit playing."),
            <.p("If you decide to register after playing, ",
              "we will keep your progress with your new account."))))

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
