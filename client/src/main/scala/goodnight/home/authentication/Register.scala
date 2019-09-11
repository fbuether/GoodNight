
package goodnight.home.authentication

import play.api.libs.json.Json

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.Page
import goodnight.client.Pages
import goodnight.client.{ Request, Reply }
import goodnight.components.Shell
import goodnight.components.Input


object Register extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    Pages.Register.getRoute(dsl) ~> renderR(r =>
      Shell.component(Shell.Props(r,
        "Boring Envelope.png", "Register"))(
        this.component(Props(r))))
  }

  case class Props(
    router: RouterCtl[Pages.Page]
  )

  case class State(
    error: Option[String]
  )

  class Backend(bs: BackendScope[Props, State]) {
    private val usernameRef = Ref.toScalaComponent(Input.component)
    private val mailRef = Ref.toScalaComponent(Input.component)
    private val passwordRef = Ref.toScalaComponent(Input.component)

    def doRegister(e: ReactEventFromInput): Callback = {
      e.preventDefaultCB >>
      usernameRef.get.flatMap(_.backend.get).flatMap({ username =>
        mailRef.get.flatMap(_.backend.get).flatMap({ mail =>
          passwordRef.get.flatMap(_.backend.get).map({ password =>
            (username, mail, password) }) }) }).asCallback.
        flatMap({ case Some((user, mail, password)) =>
          Request.post("/api/v1/auth/signup").
            withBody(Json.obj(
              "identity" -> mail,
              "username" -> user,
              "password" -> password)).
            send.map({
              case Reply(201, reply) => // success.
                bs.props.flatMap(_.router.set(Pages.Home))

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

    def render(p: Props, s: State): VdomElement =
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

  def component = ScalaComponent.builder[Props]("Register").
    initialState(State(None)).
    renderBackend[Backend].
    build
}


// def render(router: RouterCtl[Pages.Page]) =
//   Shell.component(Shell.Props(router,
//     "Cloudy Night.png",
//     "Have a Good Night"))(
//     <.div("Registration is not yet implemented."))
// }
