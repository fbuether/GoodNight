
package goodnight.server

import controllers.Assets
import play.api.mvc.DefaultActionBuilder
import play.api.mvc.RequestHeader
import play.api.mvc.PlayBodyParsers
import play.api.mvc.Results.ImATeapot
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing._

import goodnight.client.Frontend
import goodnight.api.Stories
import goodnight.api.Profile
import goodnight.api.authentication

import goodnight.common.ApiV1


class Router(
  action: DefaultActionBuilder,
  parse: PlayBodyParsers,
  frontend: Frontend,
  authSignUp: authentication.SignUp,
  authSignIn: authentication.SignIn,
  profile: Profile,
  stories: Stories,
  assets: Assets)
    extends SimpleRouter {

  private def invalid = action { _ => ImATeapot }

  // type Routes = PartialFunction[RequestHeader, Handler]
  def routes: Routes = (header: RequestHeader) =>
  (header.method, header.target.path) match {
    // static content: the html page, as well as all assets
    case ApiV1.Frontend() => frontend.html
    case ApiV1.Asset(file) => assets.versioned(file)

    // Authentication, Registration, Sign in and out
    //
    // Registration step 1: Post data of sign-up form.
    case ApiV1.SignUp() => authSignUp.doSignUp

    // Registration step 2: Confirmation of email via token.
    // -- is this required? How about social signup?
    case ApiV1.EmailConfirm(token) => invalid

    // Confirm user data, request authentication token
    case ApiV1.Authenticate() => authSignIn.authenticate

    // Confirm sign in via a social authentication provider
    case ApiV1.SocialAuthenticate(provider) =>
      authSignIn.socialAuthenticate(provider)

    // Sign out, remove all current sessions.
    case ApiV1.SignOut() => authSignIn.signOut

    // Password reset step 1: Post reset information form.
    case ApiV1.RequestPasswordReset() => invalid

    // Password reset step 2: Post refreshed password information.
    case ApiV1.ConfirmPasswordReset(token) => invalid

    // Stories and Story Content.
    case ApiV1.Stories() => stories.showAll(header.target.queryMap)
    case ApiV1.Story(name) => stories.showOne(name)
    case ApiV1.CreateStory() => stories.create

    // Profile data
    case ApiV1.Profile(user) => profile.show(user)
    case ApiV1.Self() => profile.showSelf
  }
}
