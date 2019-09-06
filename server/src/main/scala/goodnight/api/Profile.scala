
package goodnight.api

// import javax.inject.Inject

import play.api.mvc._

import com.mohiva.play.silhouette.api.Silhouette

import goodnight.server.Router
import goodnight.api.authentication.JwtEnvironment


class Profile(
  components: ControllerComponents,
  silhouette: Silhouette[JwtEnvironment])
    extends BaseController {


  def controllerComponents: ControllerComponents =
    components

  def show = silhouette.UserAwareAction { request =>
    request.identity match {
      case Some(id) => Ok("Hello " + id)
      case None => Ok("Hello, unidentified person.")
    }
  }
}
