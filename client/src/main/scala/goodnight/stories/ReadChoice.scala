
package goodnight.stories

import java.util.UUID
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


object ReadChoice {
  case class Props(router: pages.Router, player: model.Player,
    scene: model.Scene, choice: model.Choice,
    gotoLocation: Option[model.Location] => Callback)

  def component = ScalaComponent.builder[Props]("ReadChoice").
    stateless.
    render_P(props =>
      <.div(
        <.h2(props.scene.title),
        <.p(props.scene.text),
        <.hr(),
        <.h3(props.choice.title),
        <.p(props.choice.text),
        <.div(<.button(^.className := "right",
          ^.onClick --> props.gotoLocation(None),
          <.span(^.className := "fas fa-angle-double-right"))))
    ).
    build
}

