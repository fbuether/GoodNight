
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


object CreatePlayer {
  case class Props(router: pages.Router, story: read.Story,
    child: read.PlayerState => VdomElement)
  case class State(data: Option[read.PlayerState], saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    private val playerNameRef = Input.componentRef

    def doSave(props: Props): Callback =
      bs.modState(_.copy(saving = true)) >>
      Input.withValue(playerNameRef, playerName =>
        Request(ApiV1.CreatePlayer, props.story.urlname).
          withBody(ujson.Obj("name" -> playerName)).send.
          forStatus(201).forJson[read.PlayerState].
          body.flatMap(ps =>
            bs.modState(_.copy(saving = false, data = Some(ps))).async).
          toCallback)


    def renderForm(props: Props, saving: Boolean) =
      <.div(
        <.h2("Welcome!"),
        <.p("""To read or play this story, you will need a name first.
          Please tell us how you will be addressed henceforth in this world.
          """),
        <.form(^.className := "simple centered half inset",
          ^.onSubmit ==> (_.preventDefaultCB >> doSave(props)),
          <.h2(
            <.i(^.className := "fas fa-tag label"),
            "Enter your name:"),
          playerNameRef.component(Input.Props(
            "Name", "playerName",
            List(^.autoFocus := true, ^.required := true))),
          SavingButton.render("small atRight", "far fa-check-square",
            true, saving)("Begin the adventure!")))

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
