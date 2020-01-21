
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
import goodnight.model


class Router(
  action: DefaultActionBuilder,
  parse: PlayBodyParsers,
  frontend: Frontend,
  authSignUp: authentication.SignUp,
  authSignIn: authentication.SignIn,
  profile: Profile,
  readStory: read.Story,
  readScene: read.Scene,
  readChoices: read.Choices,
  readPlayer: read.Player,
  writeStory: write.Story,
  writeScene: write.Scene,
  writeQuality: write.Quality,
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
    // returns model.User
    case Profile(user) => profile.show(user)
    case Self() => profile.showSelf


    //
    // Reading Stories
    //

    // returns: Seq[model.read.Story]
    case Stories() => readStory.getAvailableStories(header.target.queryMap)

    // returns: model.read.StoryState
    case Story(story) => readStory.getStory(story)

    // returns: model.read.PlayerState
    case CreatePlayer(story) => readPlayer.createPlayer(story)
    // returns: model.read.PlayerState
    case CreateTemporary(story) => readPlayer.createTemporary(story)

    // returns: model.read.Outcome
    case GoScene(story, scene) => readScene.goScene(story, scene)


    //
    // Editing Stories
    //

    // expects: String (the story name)
    // returns: model.edit.Content
    case CreateStory() => writeStory.createStory

    // returns: model.edit.Content
    case Content(story) => writeStory.getContent(story)

    // returns: model.edit.Scene
    case Scene(story, scene) => writeScene.getScene(story, scene)

    // expects: String (the raw scene content)
    // returns: model.edit.Scene
    case CreateScene(story) => writeScene.createScene(story)
    case SaveScene(story, scene) => writeScene.saveScene(story, scene)

    // returns: model.edit.Quality
    case Quality(story, quality) => writeQuality.getQuality(story, quality)

    // expects: String (the raw quality content)
    // returns: model.edit.Quality
    case CreateQuality(story) => writeQuality.createQuality(story)
    case SaveQuality(story, quality) => writeQuality.saveQuality(story, quality)
  }
}
