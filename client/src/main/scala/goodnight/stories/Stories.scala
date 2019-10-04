
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

object Stories {
  case class StoryData(name: String, image: String, urlname: String)
  implicit val signInDataReads: Reads[StoryData] =
    ((JsPath \ "name").read[String] and
      (JsPath \ "image").read[String] and
      (JsPath \ "urlname").read[String])(StoryData.apply _)

  // type Props = RouterCtl[pages.Page]

  // type State = (Boolean, List[String])

  def loadStories(router: RouterCtl[pages.Page]) =
    Request.get("/api/v1/stories").send.forJson.
      map({
        case Reply(_, Success(JsArray(stories))) =>
          <.ul(^.className := "storyList",
            stories.map({ storyJson =>
              val data = storyJson.as[StoryData]
              <.li(
                router.link(pages.Story(data.urlname))(
                  <.img(^.src := (router.baseUrl + "assets/images/buuf/" +
                    data.image).value),
                  <.div(data.name)))
            }).toTagMod)
        case Reply(_, f) =>
          <.p("got wrong reply: " + f)
      })


  def storyList(router: RouterCtl[pages.Page]): VdomElement =
    Loading.suspend(router, loadStories(router))


  val component = ScalaComponent.builder[RouterCtl[pages.Page]]("Stories").
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

  def render(router: RouterCtl[pages.Page]) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "The Worlds"),
      this.component(router))
}
