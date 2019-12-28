
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
import goodnight.api.Profile
import goodnight.api.authentication
import goodnight.stories.read
import goodnight.stories.write

import goodnight.common.ApiV1._


class Router(
  action: DefaultActionBuilder,
  parse: PlayBodyParsers,
  frontend: Frontend,
  authSignUp: authentication.SignUp,
  authSignIn: authentication.SignIn,
  profile: Profile,
  readStories: read.Stories,
  readScenes: read.Scenes,
  readChoices: read.Choices,
  readPlayer: read.Player,
  writeStories: write.Stories,
  writeScenes: write.Scenes,
  assets: Assets)
    extends SimpleRouter {

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
    case EmailConfirm(token) => ???

    // Confirm user data, request authentication token
    // returns Accepted(model.User) on success.
    case Authenticate() => authSignIn.authenticate

    // Confirm sign in via a social authentication provider
    case SocialAuthenticate(provider) => ???

    // Sign out, remove all current sessions.
    case SignOut() => authSignIn.signOut

    // Password reset step 1: Post reset information form.
    case RequestPasswordReset() => ???

    // Password reset step 2: Post refreshed password information.
    case ConfirmPasswordReset(token) => ???

    // Profile data
    case Profile(user) => profile.show(user)
    case Self() => profile.showSelf


    //
    // Reading Stories
    //
    case Stories() => readStories.getAvailableStories(header.target.queryMap)
    case Story(story) => readStories.getStory(story)

    case CreatePlayer(story) => readPlayer.createPlayer(story)
    case DoScene(story, scene) => readScenes.doScene(story, scene)


    //
    // Editing Stories
    //
  }
}
