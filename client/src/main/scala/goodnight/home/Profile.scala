
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.components.Banner
import goodnight.components.Loading
import goodnight.components.Shell
import goodnight.model
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }
import goodnight.stories.StoryList


object Profile {
  case class Props(router: pages.Router)
  case class State(i: Int)

  class Backend(bs: BackendScope[Props, State]) {
    def render(props: Props, state: State): VdomElement =
      <.div(
        <.h2("Profile"),
        <.p("This area will show a bit of info about yourself, at some point."),
        <.h2("My Stories"),
        StoryList.component(StoryList.Props(props.router, query ="authorMyself",
          storyPage = pages.EditStory(_))),
        <.p("Fancy something not read before? ",
          props.router.link(pages.CreateStory)(
            "Create a new story!")),
        <.p("To help you get started, we have a ",
          <.a(^.href := "https://goodnight.jasminefields.net/documentation/",
            "documentation how to create stories with GoodNight"),
          "."))
  }

  val component = ScalaComponent.builder[Props]("Profile").
    initialState(State(2)).
    renderBackend[Backend].
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router,
        "Excuse me, that's just the Henny man....png", "Profile"),
      this.component(Props(router)))
}
