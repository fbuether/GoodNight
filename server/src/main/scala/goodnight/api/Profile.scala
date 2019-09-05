
package goodnight.api

// import javax.inject.Inject

import play.api.mvc._

import goodnight.server.Router


class Profile(components: ControllerComponents, router: Router)

// class Profile // @Inject() (
//   val controllerComponents: ControllerComponents,
//   configuration: Configuration
// )
extends BaseController {


  def controllerComponents: ControllerComponents =
    components

  def show = Action {
    Ok("hello.")
  }
}
