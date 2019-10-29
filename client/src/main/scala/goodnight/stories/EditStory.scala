
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.util.{ Try, Success, Failure }
import play.api.libs.json._
import play.api.libs.functional.syntax._

import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.service.Conversions._
import goodnight.components.Input
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.components.Loading

import goodnight.common.ApiV1
import goodnight.model
import goodnight.common.api.Story._

object EditStory {
  type Props = (pages.Router, String)

  val component = ScalaComponent.builder[Props]("EditStory").
    render_P(p =>
      <.div(
        <.h2("Edit Story: " + p._2),
        <.p("This is where you edit your story."),
        <.h3("Edit Metadata"),
        <.h3("Edit Locations"),
        <.h3("Edit Qualities"),
        <.h3("Edit Scenes"))).
    build

  def loadStory(router: pages.Router, name: String): AsyncCallback[VdomElement] =
    Request(ApiV1.Story, name).send.forJson.map({
      case Reply(200, Success(storyJson)) =>
        this.component(router, name)
      case e =>
        <.div("Error :( -> " + e)
    })


  def render(page: pages.EditStory, router: pages.Router) =
    Shell.component(router)(
      Banner.component((router, "Alien World.png", "World:"+page.name)),
      Loading.suspend(router, loadStory(router, page.name)))
}
