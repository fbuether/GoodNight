
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.Serialise._
import goodnight.components.Banner
import goodnight.components.Loading
import goodnight.components.Shell
import goodnight.model
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object Stories {

  // type Props = pages.Router

  // type State = (Boolean, List[String])

  def renderStory(router: pages.Router, story: model.Story) =
    <.li(
      router.link(pages.Story(story.urlname))(
        <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
          story.image).value),
        <.div(story.name)))


  def loadStories(router: pages.Router) =
    Request.get("/api/v1/stories").send.
      forJson[List[model.Story]].
      map({
        case Reply(_, stories) =>
          <.ul(^.className := "story-list as-tiles links",
            stories.map({ story =>
              renderStory(router, story)
            }).toTagMod)
        case Reply(_, f) =>
          <.p("got wrong reply: " + f)
      })


  def storyList(router: pages.Router): VdomElement =
    Loading.suspend(router, loadStories(router))


  val component = ScalaComponent.builder[pages.Router]("Stories").
    render_P(router =>
      <.div(
        <.h2("Available Stories"),
        <.p("""Stories in GoodNight exist within a world. Stories
          from the same world share a common theme, maybe even
          characters and locations. The following shows all worlds
          and the stories within."""),
        <.h2("All Stories"),
        storyList(router))).
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "The Worlds"),
      this.component(router))
}
