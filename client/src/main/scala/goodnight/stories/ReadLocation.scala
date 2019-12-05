
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


object ReadLocation {
  case class Props(router: pages.Router, player: model.Player,
    location: Option[model.Location], scenes: Seq[model.Scene],
    onClick: model.Scene => Callback)

  def component = ScalaComponent.builder[Props]("ReadLocation").
    stateless.
    render_P(props =>
      <.div(
        <.h2(props.player.name,
          props.location.map(l => TagMod(", Willkommen in " + l.name)).
            getOrElse(TagMod(", Willkommen."))),

        // todo: location description

        <.p(^.className := "call",
          "Was möchtest du hier tun?"),

        <.ul(^.className := "choices as-items",
          props.scenes.map(scene =>
            <.li(
              // leading image
              // <.img(^.className := "left",
              //   ^.src := ("assets/images/buuf/" +
              //     "I can help you my son, I am Paddle Paul..png"
              //   )),

              // requirements
              // <.ul(^.className := "requirements as-icons",
              //   <.li(^.className := "tooltip-anchor",
              //     <.img(^.src := "assets/images/buuf/" + "Plasma TV.png"),
              //     <.div(^.className := "tooltip",
              //       <.strong("Rohe Kraft"),
              //       <.span("benötigt: 20"),
              //       <.span("du hast: 27"))),
              //   <.li(^.className := "tooltip-anchor",
              //     <.img(^.src := "assets/images/buuf/" + "Chea.png"),
              //     <.div(^.className := "tooltip",
              //       <.strong("Hammer"),
              //       <.span("benötigt: vorhanden"),
              //       <.span("du hast: vorhanden")))),

              <.h4(scene.title),
              <.p(scene.text,
                <.button(^.className := "right",
                  ^.onClick --> props.onClick(scene),
                  <.span(^.className := "fas fa-angle-double-right")))
            )
          ).toTagMod
        ))
    ).
    build
}

