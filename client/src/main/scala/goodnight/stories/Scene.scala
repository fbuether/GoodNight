
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.read
import goodnight.model.Expression
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Scene {
  case class Props(router: pages.Router, story: read.Story,
    player: read.Player, state: read.States,
    effects: read.States, scene: read.Scene,
    goto: String => Callback)
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {
    def renderTest(router: pages.Router, state: read.States,
      test: read.Test) =
      <.li(^.className := "tooltip-anchor" +
        (if (test.succeeded) "" else " disabled"),
        Image.render(router, test.quality.image),
        <.div(^.className := "tooltip",
          <.strong(test.quality.name),
          <.span("required: ", test.description),
          (if (!test.succeeded) <.span("you do not have this.") else <.span())))

    def renderChoice(router: pages.Router, state: read.States,
      goto: String => Callback,
      choice: read.Choice) =
      <.li(^.className := (if (choice.available) "" else "disabled"),
        ^.title :=
          (if (choice.available) ""
          else "You do not meet the requirements for this choice."),
        <.ul(^.className := "requirements as-icons",
          choice.tests.map(renderTest(router, state, _)).
            toTagMod),
        Markdown.component(choice.text, 3)(
          if (choice.available)
            <.button(^.className := "right",
              ^.alt := "Pursue this choice",
              ^.onClick --> goto(choice.urlname),
              <.span(^.className := "fas fa-angle-double-right"))
          else
            <.span()))

    def renderEffect(router: pages.Router, effect: read.State) =
      <.li(
        Image.render(router, effect.quality.image),
        <.em(
          (effect match {
            case read.State.Bool(_, v) =>
              if (v) TagMod("You now have ", <.strong(effect.quality.name), ".")
              else TagMod(<.strong(effect.quality.name), " is now gone!")
            case read.State.Integer(_, v) =>
              TagMod(<.strong(effect.quality.name),
                " is now " + v.toString + ".") })))

    def render(props: Props, state: State) =
      <.div(
        Markdown.component(props.scene.text, 1)(),
        <.ul(^.className := "effects",
          props.effects.map(renderEffect(props.router, _)).toTagMod),
        <.ul(^.className := "choices as-items",
          props.scene.choices.map(renderChoice(props.router, props.state,
            props.goto, _)).
            toTagMod
        ))
  }

  def component = ScalaComponent.builder[Props]("StoryRoll").
    initialState(State(())).
    renderBackend[Backend].
    build
}
