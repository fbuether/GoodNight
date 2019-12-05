
package goodnight.stories

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
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object Stories {
  def renderStory(router: pages.Router, story: model.Story) =
    <.li(
      router.link(pages.Story(story.urlname))(
        <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
          story.image).value),
        <.div(story.name)))

  def renderStories(router: pages.Router, stories: Seq[model.Story]) =
    <.ul(^.className := "story-list as-tiles links",
      stories.map(renderStory(router, _)).toTagMod)

  def loadStories(router: pages.Router) =
    Request(ApiV1.Stories).send.
      forStatus(200).
      forJson[List[model.Story]].
      body.
      map(renderStories(router, _))

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
