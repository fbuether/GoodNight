
package goodnight.client

import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.Mode
import controllers.AssetsFinder


class Frontend(
  components: ControllerComponents,
  assetsFinder: AssetsFinder,
  mode: Mode)
    extends BaseController {

  def controllerComponents: ControllerComponents =
    components

  def html = Action {
    val resolver = assetsFinder.path _
    val mainJs = if (mode == Mode.Prod) "opt" else "fastopt"

    Ok(goodnight.client.html.frontend(mainJs, resolver))
  }
}
