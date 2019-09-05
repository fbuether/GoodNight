
package goodnight.api

import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents


class Users(components: ControllerComponents) extends BaseController {

  def controllerComponents: ControllerComponents =
    components

  def getUser(user: String) = Action {
    Ok("good enough")
  }
}
