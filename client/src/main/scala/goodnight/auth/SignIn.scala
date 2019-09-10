
package goodnight.auth

import play.api.libs.json.Json

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.component.builder.Lifecycle.RenderScope

import goodnight.client.Page
import goodnight.client.Pages
import goodnight.client.Request
import goodnight.components.Shell


object SignIn extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    Pages.SignIn.getRoute(dsl) ~> renderR(r => this.component(Props(r)))
  }


  case class Props(
    router: RouterCtl[Pages.Page]
  )

  case class State(
    username: String,
    password: String,
    loginError: Option[String]
  )

  class Backend(bs: BackendScope[Props, State]) {
    val usernameRef = Ref[html.Input]
    val passwordRef = Ref[html.Input]

    def doSignIn(e: ReactEventFromInput): Callback = {
      e.preventDefaultCB >>
      usernameRef.get.
        flatMap({ username => passwordRef.get.
          map({ password => (username.value, password.value) }) }).asCallback.
        flatMap({ case Some((user, pass)) =>
          Request.post("/api/v1/auth/authenticate").
            withBody(Json.obj(
              "identity" -> user,
              "password" -> pass)).
            send.flatMap({
              case ((202, body)) => // success.
                bs.modState(_.copy(loginError = Some("success"))).
                  asAsyncCallback

              case ((401, body)) => // wrong credentials.
                bs.modState(_.copy(loginError = Some(body))).
                  asAsyncCallback

              case ((status, body)) => // any other error.
                Callback(println(s"got ($status) $body")).
                  asAsyncCallback
            }).
            toCallback

          case None =>
            Callback(println("login unsuccessful."))
        })
    }

    def render(p: Props, s: State): VdomElement =
      Shell.component(Shell.Props(p.router,
        "A Proper Journal Icon.png",
        "Sign In",
        <.div(^.className := "withColumns",
          <.div(^.className := "column oftwo left",
            <.h2("Register"),
            <.p("If you are new to GoodNight, you can register yourself here:"),
            <.p(
              p.router.link(Pages.Register)(
                <.span(^.className := "fa fa-arrow-right"),
                " Register yourself at GoodNight")),
            <.h2("Forgot Password?"),
            <.p("Forgot your password?"),
            <.p(
              p.router.link(Pages.RequestPasswordReset)(
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
          <.div(^.className := "column oftwo right",
            <.form(^.className := "centered inset",
              ^.onSubmit ==> doSignIn,
              <.h2(
                <.span(^.className := "fa fa-check-square-o"),
                " Sign in"),
              <.p(^.className := "plain error"),
              <.label(^.className := "captioned",
                "Email or Username:",
                <.input.text(^.name := "username",
                  ^.defaultValue := s.username,
                  ^.required := true,
                  ^.autoFocus := true,
                  ^.tabIndex := 1).withRef(usernameRef)),
              <.label(^.className := "captioned",
                "Password:",
                <.input.password(^.name := "password",
                  ^.defaultValue := s.username,
                  ^.required := true,
                  ^.tabIndex := 2).withRef(passwordRef)),
              // <label class="checkbox">
              //   <input type="hidden" name="staySignedInExists" value="true">
              //   <input type="checkbox" name="staySignedIn" value="true"
              //          tabindex="3" />
              //   Stay signed in
              // </label>
              <.button(^.tpe := "submit",
                ^.tabIndex := 3,
                <.span(^.className := "fa fa-pencil-square-o"),
                " Sign in"),
              s.loginError.map(err =>
                <.p(err)))))
      ))
  }

  def component = ScalaComponent.builder[Props]("SignIn").
    initialState(State("", "", None)).
    renderBackend[Backend].
    build
}

