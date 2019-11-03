
package goodnight.home.authentication

import play.api.libs.json.Json

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router._


import goodnight.client.pages
import goodnight.service.{ Request, Reply }
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.components.Input
import goodnight.common.ApiV1
import goodnight.service._


object SignIn {
  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "A Proper Journal Icon.png", "Sign In"),
      this.component(Props(router, None)))

  def renderFor(page: pages.SignInFor, router: pages.Router) =
    Shell.component(router)(
      Banner.component(router, "A Proper Journal Icon.png",
        "Sign In to view this page"),
      this.component(Props(router, Some(page.page))))

  case class Props(
    router: pages.Router,
    target: Option[String]
  )

  case class State(
    loginError: Option[String],
    loading: Boolean
  )

  class Backend(bs: BackendScope[Props, State]) {
    private val usernameRef = Ref.toScalaComponent(Input.component)
    private val passwordRef = Ref.toScalaComponent(Input.component)

    def getEmpty: Option[(String, String)] = None

    def reportMissingData: CallbackOption[(String, String)] =
      bs.modState(
        _.copy(loginError =
          Some("Please fill in all required fields."),
          loading = false)).
        map(_ => getEmpty).asCBO

    def getFormData: CallbackOption[(String, String)] =
      usernameRef.get.flatMap(_.backend.get).flatMap({ username =>
        passwordRef.get.flatMap(_.backend.get).map({ password =>
          (username, password) }) }).
        orElse(reportMissingData)

    def handleReply(reply: Reply[String]) = reply match {
      case Reply(202, _) => // Accepted
        bs.props.flatMap(props => {
          println("well, working on it: " + props)
          println("user:" + AuthenticationService.getUser)
          props.target match {
            case Some(target) =>
              println("logging in to " + target)
              props.router.byPath.set(Path(target))
            case None => props.router.set(pages.Profile)
          }
        })
      case Reply(403, body) => // Unauthorized
        bs.modState(_.copy(
          loginError =
            Some("Error: Email or Password is wrong. Please try again."),
          loading = false))
      case Reply(status, body) =>
        Callback(println(s"got ($status) $body")) >>
        bs.modState(_.copy(
          loginError = Some("An unknown error occured."),
          loading = false))
    }

    def doSignIn(e: ReactEventFromInput): Callback = {
      e.preventDefaultCB >>
      bs.modState(_.copy(loading = true)) >>
      getFormData.flatMap({ case (user, pass) =>
          Request(ApiV1.Authenticate).
            withBody(Json.obj(
              "identity" -> user,
              "password" -> pass)).
            send.map(handleReply _).
            flatMap(_.asAsyncCallback).
            toCallback
      })
    }

    def render(p: Props, s: State): VdomElement = {
      println("rendering with " + p + "/" + s)
      <.div(^.className := "withColumns",
        <.div(
          <.h2("Register"),
          <.p("If you are new to GoodNight, you can register yourself here:"),
          <.p(
            p.router.link(pages.Register)(
              <.i(^.className := "fa fa-arrow-right"),
              " Register yourself at GoodNight")),
          <.h2("Forgot Password?"),
          <.p("Forgot your password?"),
          <.p(
            p.router.link(pages.RequestPasswordReset)(
              <.i(^.className := "fa fa-unlock"),
              " Request to reset your password")),
          <.h2("Social Media Sign In"),
          <.p("Sign in via any of these social media providers:"),
          // <.ul(
          //   <.li(
          //     p.router.link(Pages.SocialMediaSignIn("github"))(
          //       <.i(^.className := "fa fa-github"),
          //       "Github")))
        ),
        <.div(
          <.form(^.className := "centered inset",
            ^.onSubmit ==> doSignIn,
            <.h2(
              <.i(^.className := "fa fa-check-square-o"),
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
              (^.className := "loading").when(s.loading),
              (^.disabled := true).when(s.loading),
              (<.i(^.className := "fa fa-pencil-square-o")).when(!s.loading),
              (<.i(^.className := "far fa-spin fa-compass")).when(s.loading),
              " Sign in"),
            s.loginError.map(err =>
              <.p(^.className := "plain error",
                err)))))
    }
  }

  val component = ScalaComponent.builder[Props]("SignIn").
    initialState(State(None, false)).
    renderBackend[Backend].
    build
}

