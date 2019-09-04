
package goodnight.client

import javax.inject.Inject

import play.api.mvc._
import play.api.Configuration


class Frontend @Inject() (val controllerComponents: ControllerComponents,
    configuration: Configuration)
      extends BaseController {

  def html = Action {
    Ok(goodnight.client.html.frontend())
  }
}
