
package goodnight.server

import controllers.Assets
import play.api.mvc.DefaultActionBuilder
import play.api.mvc.PlayBodyParsers
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing._
import play.api.routing.sird._

import goodnight.client.Frontend
import goodnight.api.Authentication
import goodnight.api.authentication
import goodnight.api.Profile

class Router(
  action: DefaultActionBuilder,
  parse: PlayBodyParsers,
  frontend: Frontend,
  auth: Authentication,
  authSignUp: authentication.SignUp,
  authSignIn: authentication.SignIn,
  profile: Profile,
  assets: Assets)
    extends SimpleRouter {

  def routes: Routes = {
    // static content: the html page, as well as all assets
    case GET(p"/") => frontend.html
    case GET(p"/assets/$file*") => assets.versioned(file)


    // Authentication, Registration, Sign in and out

    // Registration step 1: Post data of sign-up form.
    case POST(p"/api/v1/auth/signup") => authSignUp.doSignUp

    // Registration step 2: Confirmation of email via token.
    // -- is this required? How about social signup?
    // case POST(p"/api/v1/auth/signup/$token") =>


    // Confirm user data, request authentication token
    case POST(p"/api/v1/auth/authenticate") => authSignIn.authenticate

    // Confirm sign in via a social authentication provider
    case POST(p"/api/v1/auth/authenticate/social/$provider") =>
      authSignIn.socialAuthenticate(provider)

    // Sign out, remove all current sessions.
    // case POST(p"/api/v1/auth/signout/") =>

    // Password reset step 1: Post reset information form.
    // case POST(p"/api/v1/auth/reset/") =>

    // Password reset step 2: Post refreshed password information.
    // case POST(p"/api/v1/auth/reset/$token") =>



    // Profile data
    case GET(p"/api/v1/profile") => profile.show
  }
}
