
package goodnight.client

// import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.components.Shell


object SignIn extends Page {
  def route(dsl: RouterConfigDsl[Pages.Page]) = {
    import dsl._
    staticRoute("#" / "sign-in", Pages.SignIn) ~> renderR(this.render)
  }

  def render(router: RouterCtl[Pages.Page]) =
    Shell.component(Shell.Props(router,
      "A Proper Journal Icon.png",
      "Sign In",

      <.div("inner text.")

    ))
/*
       <form method="post" action="https://goodnight.jasminefields.net/goodnight/signIn" class="centered inset">
  <h2><span class="fa fa-check-square-o"></span> Sign in</h2>

  <p class="plain error"></p>

  <label class="captioned">
    Email or Username:
    <input type="text" name="username"
           required autofocus tabindex="1" />
  </label>

  <label class="captioned">
    Password:
    <input type="password" name="password"
           required tabindex="2" />
  </label>

  <label class="checkbox">
    <input type="hidden" name="staySignedInExists" value="true">
    <input type="checkbox" name="staySignedIn" value="true"
           tabindex="3" />
    Stay signed in
  </label>

  <button type="submit" tabindex="4">
    <span class="fa fa-pencil-square-o"></span>
    Sign in
  </button>

  <a class="option" href="https://goodnight.jasminefields.net/goodnight/resetPassword" tabindex="4">
    Forgot your password?
  </a>

  <a class="option" href="https://goodnight.jasminefields.net/goodnight/register" tabindex="5">
    No account? Sign up
  </a>
</form>
 */
}

