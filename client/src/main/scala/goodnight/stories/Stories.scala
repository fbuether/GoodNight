
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
  val component = ScalaComponent.builder[pages.Router]("Stories").
    render_P(router =>
      <.div(
        <.h2("Available Stories"),
        <.p("""Stories in GoodNight exist within a world. Stories
          from the same world share a common theme, maybe even
          characters and locations. The following shows all worlds
          and the stories within."""),
        <.h2("All Stories"),
        StoryList.component(StoryList.Props(router)))).
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "The Worlds"),
      this.component(router))
}
