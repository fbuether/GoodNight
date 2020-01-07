
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


object Profile {
  case class Props(router: pages.Router)
  case class State(i: Int)

  class Backend(bs: BackendScope[Props, State]) {
    val changer = bs.modState(s => s.copy(i = s.i + 1))

  def renderStory(router: pages.Router, story: model.Story) =
    <.li(
      router.link(pages.EditStory(story.urlname))(
        <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
          story.image).value),
        <.div(story.name)))

    def loadMyStories(router: pages.Router) =
      Request.get(ApiV1.Stories).query("authorMyself").send.
        forJson[List[model.Story]].
        map({
          case Reply(_, stories) =>
            if (stories.isEmpty)
              <.p("You have not written any stories yet.")
            else
              <.ul(^.className := "story-list as-tiles links",
                stories.map({ story =>
                  renderStory(router, story)
                }).toTagMod)
        })

    def render(p: Props, s: State): VdomElement =
      <.div(
        <.h2("Profile"),
        <.p("This area will show a bit of info about yourself, at some point."),
        <.h2("My Stories"),
        Loading.suspend(p.router, loadMyStories(p.router)),
        <.p("Fancy something not read before? ",
          p.router.link(pages.CreateStory)(
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
