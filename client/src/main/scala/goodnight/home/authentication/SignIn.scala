
package goodnight.home.authentication

import play.api.libs.json.Json

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope
import japgolly.scalajs.react.component.Scala.MountedImpure

import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.components.Shell
import goodnight.components.Input


object SignIn {
  def render(router: RouterCtl[pages.Page]): VdomElement = {
    Shell.component(Shell.Props(router,
      "A Proper Journal Icon.png",
      "Sign In"))(
      this.component(Props(router)))
  }


  case class Props(
    router: RouterCtl[pages.Page]
  )

  case class State(
    loginError: Option[String]
  )

  class Backend(bs: BackendScope[Props, State]) {
    private val usernameRef = Ref.toScalaComponent(Input.component)
    private val passwordRef = Ref.toScalaComponent(Input.component)

    def doSignIn(e: ReactEventFromInput): Callback = {
      e.preventDefaultCB >>
      usernameRef.get.flatMap(_.backend.get).
        flatMap({ username =>
          passwordRef.get.flatMap(_.backend.get).
            map({ password => (username, password) }) }).asCallback.
        flatMap({ case Some((user, pass)) =>
          Request.post("/api/v1/auth/authenticate").
            withBody(Json.obj(
              "identity" -> user,
              "password" -> pass)).
            send.map({
              case Reply(202, body) => // success.
                bs.props.flatMap(_.router.set(pages.Home))

              case Reply(401, body) => // wrong credentials.
                bs.modState(_.copy(loginError = Some(
                  "Error: Username or password is wrong.")))

              case Reply(status, body) => // any other error.
                Callback(println(s"got ($status) $body"))
            }).flatMap(_.asAsyncCallback).toCallback

          case None =>
          Callback(())
          // bs.modState(_.copy(error =
          //   Some("Please fill in all required fields.")))
        })
    }

    def render(p: Props, s: State): VdomElement =
      <.div(^.className := "withColumns",
        <.div(
          <.h2("Register"),
          <.p("If you are new to GoodNight, you can register yourself here:"),
          <.p(
            p.router.link(pages.Register)(
              <.span(^.className := "fa fa-arrow-right"),
              " Register yourself at GoodNight")),
          <.h2("Forgot Password?"),
          <.p("Forgot your password?"),
          <.p(
            p.router.link(pages.RequestPasswordReset)(
              <.span(^.className := "fa fa-unlock"),
              " Request to reset your password")),
          <.h2("Social Media Sign In"),
          <.p("Sign in via any of these social media providers:"),
          // <.ul(
          //   <.li(
          //     p.router.link(Pages.SocialMediaSignIn("github"))(
          //       <.span(^.className := "fa fa-github"),
          //       "Github")))
        ),
        <.div(
          <.form(^.className := "centered inset",
            ^.onSubmit ==> doSignIn,
            <.h2(
              <.span(^.className := "fa fa-check-square-o"),
              " Sign in"),
            usernameRef.component(Input.Props(
              "Email:", "username",
              List(^.autoFocus := true, ^.required := true))),
            passwordRef.component(Input.Props(
              "Password:", "password",
              List(^.required := true), password = true)),
            // <label class="checkbox">
            //   <input type="hidden" name="staySignedInExists" value="true">
            //   <input type="checkbox" name="staySignedIn" value="true"
            //          tabindex="3" />
            //   Stay signed in
            // </label>
            <.button(^.tpe := "submit",
              <.span(^.className := "fa fa-pencil-square-o"),
              " Sign in"),
            s.loginError.map(err =>
              <.p(^.className := "plain error",
                err)))))
  }

  def component = ScalaComponent.builder[Props]("SignIn").
    initialState(State(None)).
    renderBackend[Backend].
    build
}

