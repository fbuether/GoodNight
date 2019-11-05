
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

object CreateStory {
  case class State(requestSent: Boolean)

  class Backend(bs: BackendScope[pages.Router, State]) {
    private val nameRef = Input.componentRef


    def createCreateRequest(name: String): AsyncCallback[Reply[Try[JsValue]]] =
      Request.put(ApiV1.CreateStory).
        withBody(Json.obj("name" -> name)).
        send.forJson

    def handleReply(repl: Reply[Try[JsValue]]): AsyncCallback[Unit] = {
      repl match {
        case Reply(200, Success(storyJson)) =>
          val story = storyJson.as[model.Story]
          (bs.props >>= (r => r.set(pages.EditStory(story.urlname)))).async
      }
    }

    def maybeRequest(name: Option[String]): AsyncCallback[Unit] = {
      name match {
        case None | Some("") =>
          Callback({
            println("No name supplied.")
          }).async.void
        case Some(name) =>
          createCreateRequest(name) >>=
          handleReply
      }
    }

    def doCreateStory =
      bs.modState(_.copy(requestSent = true)) >>
        (nameRef.get.flatMap(_.backend.get).asCallback.async >>=
          maybeRequest).toCallback

    def render(router: pages.Router, state: State): VdomElement =
      <.div(
        <.h2("A name for your story"),
        <.p("Every great story starts somewhere."),
        <.p("Yours starts with a name:"),
        nameRef.component(Input.Props("name", "Name of your Story:")),
        <.p("""You can change that name later, as long as the story is not
          published."""),
        state.requestSent match {
          case false =>
            <.button(^.tpe := "submit",
              ^.onClick --> doCreateStory,
              <.i(^.className := "fas fa-pencil-alt"),
              " Create a new story")
          case true =>
            Loading.component(router)
        }
      )
  }

  val component = ScalaComponent.builder[pages.Router]("CreateStory").
    initialState(State(false)).
    renderBackend[Backend].
    build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Alien World.png", "Create a new world"),
      this.component(router))
}
