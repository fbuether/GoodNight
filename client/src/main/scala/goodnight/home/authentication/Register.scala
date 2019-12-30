
package goodnight.home.authentication

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.components.Banner
import goodnight.components.Input
import goodnight.components.Shell
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


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
          Request(ApiV1.SignUp).
            withBody(ujson.Obj(
              "identity" -> mail,
              "username" -> user,
              "password" -> password)).
            send.map({
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
      <.form(^.className := "simple centered inset",
        ^.onSubmit ==> doRegister,
        <.h2(
          <.i(^.className := "fa fa-plus-square"),
          " Register"),
        <.p("Please supply a username and an email address, so we know how " +
          "to address as well as contact you."),
        usernameRef.component(Input.Props(
          "Username", "username",
          List(^.autoFocus := true, ^.required := true))),
        mailRef.component(Input.Props(
          "Email", "email", List(^.required := true))),
        <.p("We prefer you to supply a safe password."),
        passwordRef.component(Input.Props(
          "Password", "password",
          List(^.required := true), password = true)),
        <.button(^.tpe := "submit",
          <.i(^.className := "far fa-check-square"),
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
