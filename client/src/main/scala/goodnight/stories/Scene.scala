
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


object Scene {
  case class Props(router: pages.Router, story: model.Story,
    player: model.Player, scene: model.SceneView,
    goto: String => Callback)
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {

    def render(props: Props, state: State) =
      <.div(
        Markdown.component(props.scene.text, 1)(),
        <.ul(^.className := "choices as-items",
          props.scene.choices.map(choice =>
            <.li(
              <.ul(^.className := "requirements as-icons",
                choice.tests.map(test =>
                  <.li(^.className := "tooltip-anchor",
                    <.img(^.src := (props.router.baseUrl +
                      "assets/images/buuf/" +
                      test.quality.image).value),
                    <.div(^.className := "tooltip",
                      <.strong(test.quality.name),
                      <.span("required: ", test.minimum),
                      <.span(
                        if (test.hasMin) "true" else "false",
                        "/",
                        test.chance)))
                ).toTagMod
              ),
              Markdown.component(choice.text, 3)(
                <.button(^.className := "right",
                  ^.alt := "Pursue this choice",
                  ^.onClick --> props.goto(choice.urlname),
                  <.span(^.className := "fas fa-angle-double-right"))))
          ).toTagMod
        ))
  }

  def component = ScalaComponent.builder[Props]("StoryRoll").
    initialState(State(())).
    renderBackend[Backend].
    build
}

/*
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
 */
