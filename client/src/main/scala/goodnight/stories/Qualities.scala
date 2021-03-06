
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillUpdate

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model.read
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Qualities {
  case class Props(router: pages.Router, story: read.Story,
    state: read.States)
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {
    def renderSmallQuality(router: pages.Router, story: read.Story,
      state: read.State) =
      <.li(^.className := "small",
        // todo: link to a quality scene, if one exists
        // router.link(pages.Quality(story.urlname, state.quality.urlname))(
        <.span(
          ^.className := "quality",
          // ^.title := state.quality.description,
          Image.component(router, state.quality.image),
          <.span(state.quality.name)),
        <.span(^.className := "level", (state match {
          case read.State.Bool(_, value) => ""
          case read.State.Integer(_, value) => value.toString
        })))

    def render(props: Props, state: State): VdomElement =
      <.ul(^.className := "quality",
        props.state.filter({
          case read.State.Bool(_, v) => v
          case read.State.Integer(_, v) => v != 0 }).
          map(renderSmallQuality(props.router, props.story, _)).
        toTagMod)

        // <.li(^.className := "big",
        //   <.a(^.href := "#",
        //     <.img(
        //       ^.src := "assets/images/buuf/" +
        //         "Plasma TV.png"),
        //     <.span("Rohe Kraft"),
        //     <.span(^.className := "level",
        //       "(27)"))),
        // <.li(^.className := "small",
        //   <.a(^.href := "#",
        //     <.img(
        //       ^.src := "assets/images/buuf/" +
        //         "Tree.png"),
        //     <.span("Vitalität")),
        //   <.span(^.className := "level",
        //     "(22)")),
        // <.li(^.className := "tiny",
        //   <.a(^.href := "#",
        //     <.span("Hammer"))),
        // <.li(^.className := "tiny",
        //   <.a(^.href := "#",
        //     <.span("Wurstbrot")),
        //   <.span(^.className := "level",
        //     "(22)")))
  }

  val component = ScalaComponent.builder[Props]("Qualities").
    initialState(State(())).
    renderBackend[Backend].
    build
}
