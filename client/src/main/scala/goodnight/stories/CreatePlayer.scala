
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


object CreatePlayer {
  case class Props(router: pages.Router, story: model.Story,
    child: model.Player => VdomElement)

  case class State(player: Option[model.Player], saving: Boolean)
  class Backend(bs: BackendScope[Props, State]) {
    private val playerNameRef = Input.componentRef

    def callSave(storyUrlname: String, playerName: String):
        AsyncCallback[model.Player] =
      Request(ApiV1.CreatePlayer, storyUrlname).
        withBody(ujson.Obj("name" -> playerName)).
        send.
        forStatus(201).
        forJson[model.Player].
        body

    def doSave(props: Props): Callback =
      // e.preventDefaultCB >>
      bs.modState(_.copy(saving = true)) >>
      Input.withValue(playerNameRef, playerName =>
        callSave(props.story.urlname, playerName).flatMap(player =>
        bs.modState(_.copy(saving = false, player = Some(player))).async).
        toCallback)


    def renderForm(props: Props, saving: Boolean) =
      <.div(
        <.h2("Welcome!"),
        <.p("""To read or play this story, you will need a name first.
          Please tell us how you will be addressed henceforth in this world.
          """),
        <.div(^.className := "simple centered inset",
          playerNameRef.component(Input.Props(
            "Name", "playerName",
            List(^.autoFocus := true, ^.required := true))),
          <.button(^.tpe := "submit",
            ^.className := "small atRight",
            ^.onClick --> doSave(props),
            (^.className := "loading").when(saving),
            (^.disabled := true).when(saving),
            <.i(
              (^.className := "far fa-spin fa-compass label").when(saving),
              (^.className := "far fa-check-square label").when(!saving)),
            "Enter")))

    def render(props: Props, state: State) = state match {
      case State(None, saving) => renderForm(props, saving)
      case State(Some(player), _) => props.child(player)
    }
  }

  val component = ScalaComponent.builder[Props]("CreatePlayer").
    initialState(State(None, false)).
    renderBackend[Backend].
    build
}
