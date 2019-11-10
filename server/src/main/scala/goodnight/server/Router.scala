
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

import goodnight.common.ApiV1._


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
    case Frontend() => frontend.html
    case Asset(file) => assets.versioned(file)

    // Authentication, Registration, Sign in and out
    //
    // Registration step 1: Post data of sign-up form.
    case SignUp() => authSignUp.doSignUp

    // Registration step 2: Confirmation of email via token.
    // -- is this required? How about social signup?
    case EmailConfirm(token) => invalid

    // Confirm user data, request authentication token
    case Authenticate() => authSignIn.authenticate

    // Confirm sign in via a social authentication provider
    case SocialAuthenticate(provider) =>
      authSignIn.socialAuthenticate(provider)

    // Sign out, remove all current sessions.
    case SignOut() => authSignIn.signOut

    // Password reset step 1: Post reset information form.
    case RequestPasswordReset() => invalid

    // Password reset step 2: Post refreshed password information.
    case ConfirmPasswordReset(token) => invalid

    // Profile data
    case Profile(user) => profile.show(user)
    case Self() => profile.showSelf

    //
    // Reading Stories
    //
    case Stories() => stories.showAll(header.target.queryMap)
    case Story(story) => stories.showOne(story)
    case CreatePlayer(story) => stories.createPlayer(story)
    case Scenes(story) => stories.showScenes(story)
    case Scene(story, scene) => stories.showOneScene(story, scene)

    //
    // Editing Stories
    //
    case CreateStory() => stories.create
    case CreateScene(story) => stories.createScene(story)
    case EditScene(story, scene) => stories.updateScene(story, scene)

  }
}
