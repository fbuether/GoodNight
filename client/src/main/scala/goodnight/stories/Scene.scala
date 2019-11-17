
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }
import com.dbrsn.scalajs.react.markdown.ReactMarkdown

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Scene {
  case class Props(router: pages.Router, scene: model.Scene,
    player: model.Player)
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {

    def render(props: Props, state: State) =
      <.div(
        <.h2(props.scene.title),
        ReactMarkdown(source = props.scene.text)())
  }

  def component = ScalaComponent.builder[Props]("StoryRoll").
    initialState(State(())).
    renderBackend[Backend].
    build
}
