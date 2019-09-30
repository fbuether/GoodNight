
package goodnight.stories

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import play.api.libs.json._

import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.components.Shell
import goodnight.components.Banner


object Stories {
  // type Props = RouterCtl[pages.Page]

  // type State = (Boolean, List[String])

  def loadStories(router: RouterCtl[pages.Page]) =
    Request.get("/api/v1/stories").send.
      map({ case Reply(_, JsArray(stories)) =>
        <.ul(^.className := "storyList",
          stories.map({ case JsString(name) =>
            <.li(
              router.link(pages.Story(name))(
                // <.img(^....,
                name))
          }).toTagMod
        )
      })


  def storyList(router: RouterCtl[pages.Page]): VdomElement =
    React.Suspense(<.p("loading..."),
      loadStories(router))


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
