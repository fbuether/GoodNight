
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
  // def render(router: pages.Router): VdomElement = {
  //   Shell.component(Shell.Props(router,
  //     ))(
  //     component(Props(router)))
  // }


  // type Props = (pages.Router)

  case class Props(
    router: pages.Router
  )

  case class State(
    i: Int
  )

  class Backend(bs: BackendScope[Props, State]) {
    val changer = bs.modState(s => s.copy(i = s.i + 1))

  def renderStory(router: pages.Router, story: model.Story) =
    <.li(
      router.link(pages.EditStory(story.urlname))(
        <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
          story.image).value),
        <.div(story.name)))

    def loadMyStories(router: pages.Router) =
      Request.get(ApiV1.Stories).query("authorMyself").send.forJson.
        map({
          case Reply(_, Success(JsArray(stories))) =>
            if (stories.isEmpty)
              <.p("You have not written any stories yet.")
            else
              <.ul(^.className := "storyList",
                stories.map({ storyJson =>
                  renderStory(router, storyJson.as[model.Story])
                }).toTagMod)
          case Reply(_, f) =>
            <.p("got wrong reply: " + f)
        })

    def render(p: Props, s: State): VdomElement =
      <.div(
        <.h2("Profile"),
        <.p("This area will show a bit of info about yourself, at some point."),
        <.h2("My Stories"),
        Loading.suspend(p.router, loadMyStories(p.router)),
        <.ul(^.className := "storyList",
          <.li(
            p.router.link(pages.CreateStory)(
              <.img(^.src := (p.router.baseUrl +
                "assets/images/buuf/Alien World.png").value),
              <.div("Create a new story")))))
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
