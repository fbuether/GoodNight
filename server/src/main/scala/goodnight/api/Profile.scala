
package goodnight.api

// import javax.inject.Inject

import play.api.mvc._

import com.mohiva.play.silhouette.api.Silhouette

import goodnight.server.Router


class Profile(
  components: ControllerComponents,
  silhouette: Silhouette[JWTEnv])
    extends BaseController {


  def controllerComponents: ControllerComponents =
    components

  def show = silhouette.SecuredAction {
    Ok("hello.")
  }
}
