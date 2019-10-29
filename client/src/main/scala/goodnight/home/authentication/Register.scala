
package goodnight.home.authentication

import scala.util.{ Try, Success, Failure }
import play.api.libs.json.Json

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.service.Conversions._
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.components.Input

import goodnight.common.ApiV1


object Register {
  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "Boring Envelope.png", "Register"),
      this.component(router))


  type Props = pages.Router

  case class State(
    error: Option[String]
  )

  class Backend(bs: BackendScope[Props, State]) {
    private val usernameRef = Input.componentRef
    private val mailRef = Input.componentRef
    private val passwordRef = Input.componentRef

    def doRegister(e: ReactEventFromInput): Callback = {
      e.preventDefaultCB >>
      usernameRef.get.flatMap(_.backend.get).flatMap({ username =>
        mailRef.get.flatMap(_.backend.get).flatMap({ mail =>
          passwordRef.get.flatMap(_.backend.get).map({ password =>
            (username, mail, password) }) }) }).asCallback.
        flatMap({ case Some((user, mail, password)) =>
          Request.put(ApiV1.SignUp).
            withBody(Json.obj(
              "identity" -> mail,
              "username" -> user,
              "password" -> password)).
            send.forJson.map({
              case Reply(201, reply) => // success.
                bs.props.flatMap(_.set(pages.Home))

              case Reply(403, reply) => // already registered.
                bs.modState(_.copy(error = Some(
                  "Error: The email is already registered.")))

              case Reply(status, body) => // other error
                Callback(println(s"got ($status) $body"))
            }).flatMap(_.asAsyncCallback).toCallback

          case None =>
          bs.modState(_.copy(error = Some(
            "Please fill in all required fields.")))
        })
    }

    def render(router: Props, s: State): VdomElement =
      <.form(^.className := "centered inset",
        ^.onSubmit ==> doRegister,
        <.h2(
          <.span(^.className := "fa fa-plus-square"),
          " Register"),
        usernameRef.component(Input.Props(
          "Username:", "username",
          List(^.autoFocus := true, ^.required := true))),
        mailRef.component(Input.Props(
          "Email:", "email", List(^.required := true))),
        passwordRef.component(Input.Props(
          "Password:", "password",
          List(^.required := true), password = true)),
        <.button(^.tpe := "submit",
          <.span(^.className := "fa fa-pencil-square-o"),
          " Register"),
        s.error.map(err =>
          <.p(^.className := "plain error",
            err)))
  }

  val component = ScalaComponent.builder[Props]("Register").
    initialState(State(None)).
    renderBackend[Backend].
    build
}
