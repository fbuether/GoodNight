
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


object ReadScene {
  case class Props(router: pages.Router, player: model.Player,
    scene: model.Scene, choices: Seq[model.Choice],
    onClick: model.Choice => Callback)

  def component = ScalaComponent.builder[Props]("ReadScene").
    stateless.
    render_P(props =>
      <.div(
        <.h2(props.scene.title),
        <.p(props.scene.text),
        <.ul(^.className := "choices as-items",
          props.choices.sortBy(_.pos).map(choice =>
            <.li(
              <.p(choice.text,
                <.button(^.className := "right",
                  ^.onClick --> props.onClick(choice),
                  <.span(^.className := "fas fa-angle-double-right"))))
          ).toTagMod
        ))
    ).
    build
}

