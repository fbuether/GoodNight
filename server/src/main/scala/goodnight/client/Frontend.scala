
package goodnight.client

import play.filters.csrf._

import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import controllers.AssetsFinder


class Frontend(
  components: ControllerComponents,
  assetsFinder: AssetsFinder)
    extends BaseController {

  def controllerComponents: ControllerComponents =
    components

  def html = addToken {
    Action {
      val resolver = assetsFinder.path _
      Ok(goodnight.client.html.frontend(resolver))
    }
  }
}
