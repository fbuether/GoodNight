
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import scala.util.{ Try, Success, Failure }
import play.api.libs.json._
import play.api.libs.functional.syntax._

import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.service.Conversions._
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.components.Loading

import goodnight.model.Story
import goodnight.common.api.Story._

object Stories {

  // type Props = pages.Router

  // type State = (Boolean, List[String])

  def loadStories(router: pages.Router) =
    Request.get("/api/v1/stories").send.forJson.
      map({
        case Reply(_, Success(JsArray(stories))) =>
          <.ul(^.className := "storyList",
            stories.map({ storyJson =>
              val data = storyJson.as[Story]
              <.li(
                router.link(pages.Story(data.urlname))(
                  <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
                    data.image).value),
                  <.div(data.name)))
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
        <.h2("Common World"),
        storyList(router)
          )).
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "The Worlds"),
      this.component(router))
}
