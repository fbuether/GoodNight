
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


object StoryBacklog {
  case class Props(router: pages.Router, story: model.Story,
    player: model.Player, log: Seq[model.PlayerAction])

  def component = ScalaComponent.builder[Props]("StoryBacklog").
    stateless.
    render_P(props =>
      <.div(
        <.p("--Backlog"))
    ).
    build
}
