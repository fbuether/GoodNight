
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
        withBody(Json.obj(
          "name" -> name)).
        send.forJson.map({ f =>
          println("beeh -> " + f)
          f
        })


    def handleReply(router: pages.Router)(repl: Reply[Try[JsValue]]):
        AsyncCallback[Unit] = {
      println("bugh" + repl)

      // Callback({println("pre-route")}).async >>
      router.set(pages.EditStory("5")).async // >>
      // Callback({println("post-route")}).async
      // AsyncCallback.pure(())
    }

    def maybeRequest(router: pages.Router, name: Option[String]):
        AsyncCallback[Unit] = {
      name match {
        case None | Some("") =>
          Callback({
            println("No name supplied.")
          }).async.void
        case Some(name) =>
          Callback({
            println("name supplied "+ name +"!!!.")
          }).async.void >>
          createCreateRequest(name).flatMap(
          handleReply(router)) >>
          Callback({
            println("bleh. "+ name +"!!!.")
          }).async.void
      }
    }

    def doCreateStory(router: pages.Router)(e: ReactEventFromInput):
        Callback = {
      e.preventDefaultCB >>
      bs.modState(_.copy(requestSent = true)) >>
        (((nameRef.get.flatMap(_.backend.get).asCallback).async >>=
          (maybeRequest(router, _))) >>
          (Callback({ println("finished story create.") }).async)
        ).toCallback
    }

      //   e.preventDefaultCB >>
      //   // next line is AsyncCallback[Option[String]]
      //   nameRef.get.flatMap(_.backend.get).asCallback).async.delayMs(2000) >>= {
      //   case None => println("error, no name supplied.")
      //   case Some(name) =>
      //     println("At least we got the name: " + name)
      // }


      // (createCreateRequest _) >>= {
      //   // case Repl

      //   // flatMap({ name =>
      //   // Request.put(ApiV1.CreateStory).
      //   //   withBody(Json.obj(
      //   //     "name" -> name)).
      //   //   send.forJson.map({
      //   case Reply(200, Success(storyJson)) => // successfully created.
      //     val story = storyJson.as[model.Story]
      //     println("redirecting to <edit " + story.urlname + ">")
      //     router.set(pages.EditStory(story.urlname))
      // }
    // }

    //       }).flatMap(_.asAsyncCallback).toCallback
    //   })
    // }

    def render(router: pages.Router, state: State): VdomElement =
      <.form(^.className := "centered inset",
        ^.onSubmit ==> doCreateStory(router),
        <.h2("A name for your story"),
        <.p("Every great story starts somewhere."),
        <.p("Yours starts with a name:"),
        nameRef.component(Input.Props("name", "Name of your Story:")),
        <.p("""You can change that name later, as long as the story is not
          published."""),
        state.requestSent match {
          case false =>
            <.button(^.tpe := "submit",
              <.span(^.className := "fas fa-pencil-alt"),
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
