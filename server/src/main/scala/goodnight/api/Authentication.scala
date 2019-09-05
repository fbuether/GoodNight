
package goodnight.api

import javax.inject.Inject

import play.api.mvc._
import play.api.Configuration


class Authentication @Inject() (
  val controllerComponents: ControllerComponents,
  configuration: Configuration
) extends BaseController {

  def doSignUp = {}

  def confirmSignUp(token: String) = { }

  def doRequestResetPassword = { }

  def doResetPassword(token: String) = {
  }

  def authenticate = { }

  def socialAuthenticate(provider: String) = { }

  def signOut = { }
}
