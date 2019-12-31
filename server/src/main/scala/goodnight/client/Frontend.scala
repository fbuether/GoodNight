
package goodnight.client

import controllers.AssetsFinder
import play.api.Mode
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.filters.csrf.CSRFAddToken
import play.filters.csrf.CSRF

class Frontend(
  components: ControllerComponents,
  assetsFinder: AssetsFinder,
  csrfAddToken: CSRFAddToken,
  mode: Mode)
    extends BaseController {

  def controllerComponents: ControllerComponents =
    components

  def html = csrfAddToken(Action({ request =>
    val resolver = assetsFinder.path _
    val mainJs = if (mode == Mode.Prod) "opt" else "fastopt"

    // request the token, in order to write it into the reply.
    CSRF.getToken(request).get
    Ok(goodnight.client.html.frontend(mainJs, resolver))
  }))
}
