
package goodnight.api

import javax.inject.Inject

import play.api.mvc._
import play.api.Configuration


class Users @Inject() (val controllerComponents: ControllerComponents,
    configuration: Configuration)
      extends BaseController {

  def getUser(user: String) = Action {
    Ok(goodnight.client.html.frontend())
  }
}
