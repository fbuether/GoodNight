
package goodnight.stories

import java.util.UUID
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
  case class Props(router: pages.Router, story: model.Story, user: model.User,
    onSave: String => Callback)

  case class State(saving: Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    private val playerNameRef = Input.componentRef

    def doSave(e: ReactEventFromInput): Callback =
      e.preventDefaultCB >>
    bs.modState(_.copy(saving = true)) >>
    playerNameRef.get.flatMap(_.backend.get).flatMap({ playerName =>
      bs.props.flatMap(_.onSave(playerName))
    })

    def render(props: Props, state: State) = {
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
            ^.onClick ==> doSave,
            (^.className := "loading").when(state.saving),
            (^.disabled := true).when(state.saving),
            <.i(
              (^.className := "far fa-spin fa-compass label").
                when(state.saving),
              (^.className := "far fa-check-square label").when(!state.saving)),
            "Enter")))
    }
  }

  val component = ScalaComponent.builder[Props]("CreatePlayer").
    initialState(State(false)).
    renderBackend[Backend].
    build
}
