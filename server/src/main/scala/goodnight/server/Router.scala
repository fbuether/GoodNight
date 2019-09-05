
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
import goodnight.api.Profile

class Router(
  action: DefaultActionBuilder,
  parse: PlayBodyParsers,
  frontend: Frontend,
  auth: Authentication,
  profile: Profile,
  assets: Assets)
    extends SimpleRouter {

  def routes: Routes = {
    // static content: the html page, as well as all assets
    case GET(p"/") => frontend.html
    case GET(p"/assets/$file*") => assets.versioned(file)

    // Authentication

    // confirm login authentication.
    case POST(p"/api/v1/auth/authenticate") => auth.authenticate


    // Profile data
    case GET(p"/api/v1/profile") => profile.show
  }
}



/*

# The frontend application, served as a base html file that provides links,
# as well as a set of static files. These include all images, js dependencies,
# as well as the actual goodnight-client.js.
GET / goodnight.client.Frontend.html
GET /assets/ * file controllers.Assets.versioned(file)


# The server-side api. Version 1.


### Authentication

# Registration step 1: Post data of sign-up form.
POST /api/1/auth/signup goodnight.api.Authentication.doSignUp

# Registration step 2: Confirmation of email via token.
# -- is this required? How about social signup?
POST /api/1/auth/signup/:token goodnight.api.Authentication.confirmSignUp(token: String)

# Password reset step 1: Post reset information form.
POST /api/1/auth/reset/ goodnight.api.Authentication.doRequestResetPassword

# Password reset step 2: Post refreshed password information.
POST /api/1/auth/reset/:token goodnight.api.Authentication.doResetPassword(token: String)

# Confirm user data, request authentication token
POST /api/1/auth/authenticate/ goodnight.api.Authentication.authenticate

# Confirm sign in via a social authentication provider
POST /api/1/auth/social/:provider goodnight.api.Authentication.socialAuthenticate(provider: String)

# Sign out, remove all current sessions.
POST /api/1/auth/signout/ goodnight.api.Authentication.signOut




# GET /api/1/users/:name goodnight.api.Users.getUser(name: String)



# personal information.

GET /profile goodnight.api.Profile.show

 */
