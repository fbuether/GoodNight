
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillUpdate

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components._
import goodnight.model
import goodnight.service.Request
import goodnight.service.Reply
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._


object Qualities {
  case class Props(router: pages.Router, story: model.Story,
    player: model.Player)
  case class State(n: Unit)

  class Backend(bs: BackendScope[Props, State]) {
    def renderSmallQuality(router: pages.Router, story: model.Story,
      state: Map[String, String], quality: model.Quality) =
      <.li(^.className := "small",
        router.link(pages.Story(quality.urlname))(
          ^.title := quality.description,
          <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
            quality.image).value),
          <.span(quality.name)),
        state.get(quality.urlname).
          map(<.span(^.className := "level", _)).getOrElse(TagMod("")))


    val qualities = Seq(
      model.Quality("das-labyrinth",
        "$ name: Feige\n\n$ image: Hubernate.png",
        "Feige", "feige", model.Sort.Integer(Some(0), Some(10)),
        "Hubernate.png", "Wie sehr hast du Angst vor der Welt?"),
      model.Quality("das-labyrinth",
        "$ name: Gierig\n\n$ image: Q-tip has the lamest voice ever. Nice beats though..png",
        "Gierig", "gierig", model.Sort.Integer(Some(0), Some(10)),
        "Q-tip has the lamest voice ever. Nice beats though..png", "Gold. Geld. Juwelen. Egal, solange sie dir gehören."))

    def render(props: Props, state: State): VdomElement =
      <.ul(^.className := "quality",
        qualities.map(
          renderSmallQuality(props.router, props.story,
            Map("gierig" -> "8", "feige" -> "2")
            // props.player.state
              , _)).
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
