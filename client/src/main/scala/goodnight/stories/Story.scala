
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.components._
import goodnight.model
import goodnight.service.Loader


object Story {
  case class Props(router: pages.Router, storyUrlname: String)

  case class State(
    story: Option[model.Story],
    player: Option[model.Player],
    loading: Boolean
  )

  class Backend(bs: BackendScope[Props, State]) {
    def loadState =
      Callback.log("loading")

    def render(props: Props, state: State) = {
      <.div(
        Banner.component(props.router, "Alien World.png", "A world"),
        <.h2("Welcome!"),
        <.p("""Worlds in GoodNight exist within a universe. Worlds
          from the same universe share a common theme, maybe even
          characters and locations. The following shows all universes
          and the worlds within."""),
        <.h3("The World"),
        <.p("You have found the world \"" + props.storyUrlname + "\"."))
    }
  }

  val component = ScalaComponent.builder[Props]("ReadStory").
    initialState(State(None, None, false)).
    renderBackend[Backend].
    componentDidMount(_.backend.loadState).
    build

  def render(page: pages.Story, router: pages.Router) =
    Shell.component(router)(this.component(
      Props(router, page.story)))
}
