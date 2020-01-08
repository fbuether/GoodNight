
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.play
import goodnight.model.Expression
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Scene {
  case class Props(router: pages.Router, story: play.Story,
    player: play.Player, state: play.States, scene: play.Scene,
    goto: String => Callback)
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {
    def requiredOfTest(test: play.Test) = test match {
      case play.Test.Bool(_, _, value) =>
        if (value) "have this" else "do not have this"
      case play.Test.Integer(_, _, op, other) =>
        (op match {
          case Expression.Greater => "more than"
          case Expression.GreaterOrEqual => "at least"
          case Expression.Less => "less than"
          case Expression.LessOrEqual => "at most"
          case Expression.Equal => "exactly"
          case Expression.NotEqual => "not" }) +
        " " + other.toString
    }

    def haveOfTest(quality: play.Quality[_], state: play.States) =
      state.filter(_.quality.urlname == quality.urlname).headOption match {
        case Some(play.State.Bool(_, value)) =>
          if (value) "have this" else "do not have this"
        case Some(play.State.Integer(_, value)) => value.toString
        case None => quality.sort match {
          case play.Sort.Bool => "do not have this"
          case play.Sort.Integer => "0"
        }
      }

    def renderTest(router: pages.Router, state: play.States,
      test: play.Test) =
      <.li(^.className := "tooltip-anchor" +
        (if (test.succeeded) "" else " disabled"),
        Image.render(router, test.quality.image),
        <.div(^.className := "tooltip",
          <.strong(test.quality.name),
          <.span("required: ", requiredOfTest(test)),
          <.span("you have: ", haveOfTest(test.quality,
            state))))

    def renderChoice(router: pages.Router, state: play.States,
      goto: String => Callback,
      choice: play.Choice) =
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

    def render(props: Props, state: State) =
      <.div(
        Markdown.component(props.scene.text, 1)(),
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
