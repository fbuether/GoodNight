
package goodnight.server

import scala.concurrent.ExecutionContext
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents


class Controller(
  components: ControllerComponents)(
  implicit ec: ExecutionContext)
    extends BaseController {
  def controllerComponents: ControllerComponents = components
}

