
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

import goodnight.common.ApiV1
import goodnight.model
import goodnight.common.api.Story._

object CreateStory {

  class Backend(bs: BackendScope[pages.Router, Unit]) {
    private val nameRef = Input.componentRef

    def doCreateStory(router: pages.Router)(e: ReactEventFromInput):
        Callback = {
      e.preventDefaultCB >>
      nameRef.get.flatMap(_.backend.get).flatMap({ name =>
        Request.put(ApiV1.CreateStory).
          withBody(Json.obj(
            "name" -> name)).
          send.forJson.map({
            case Reply(200, Success(storyJson)) => // successfully created.
              val story = storyJson.as[model.Story]
              println("redirecting to <edit " + story.urlname + ">")
              router.set(pages.EditStory(story.urlname))
          }).flatMap(_.asAsyncCallback).toCallback
      })
    }

    def render(router: pages.Router): VdomElement =
      <.form(^.className := "centered inset",
        ^.onSubmit ==> doCreateStory(router),
        <.h2("A name for your story"),
        <.p("Every great story starts somewhere."),
        <.p("Yours starts with a name:"),
        nameRef.component(Input.Props("name", "Name of your Story:")),
        <.p("""You can change that name later, as long as the story is not
          published."""),
        <.button(^.tpe := "submit",
          <.span(^.className := "fas fa-pencil-alt"),
          " Create a new story")
      )
}

val component = ScalaComponent.builder[pages.Router]("CreateStory").
  renderBackend[Backend].
  build

def render(router: pages.Router) =
  Shell.component(router)(
    Banner.component(router, "Alien World.png", "Create a new world"),
    this.component(router))
}
