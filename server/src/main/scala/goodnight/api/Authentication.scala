
package goodnight.api

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.libs.json.JsValue
import play.api.libs.json.{ Json, Reads, JsPath, JsSuccess, JsError, JsonValidationError }
import play.api.libs.functional.syntax._

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider


import goodnight.model.User
import goodnight.api.authentication.JwtEnvironment

// trait JwtEnvironment extends Env {
//   type I = User
//   type A = JWTAuthenticator
// }



class UserService(implicit ec: ExecutionContext) extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    Future {
      Some(User(UUID.randomUUID(), "username", loginInfo))
      // None
      }
}


class Authentication(components: ControllerComponents,
  silhouette: Silhouette[JwtEnvironment])(
  implicit ec: ExecutionContext)
    extends BaseController {

  def controllerComponents: ControllerComponents =
    components



  def confirmSignUp(token: String) = Action {
    Ok("{ success: true }").as(JSON)
  }

  def doRequestResetPassword = Action {
    Ok("{ success: true }").as(JSON)
  }

  def doResetPassword(token: String) = Action {
    Ok("{ success: true }").as(JSON)
  }

  def authenticate = silhouette.UnsecuredAction { r: Request[AnyContent] =>
    Ok("{ success: true }").as(JSON)
  }

  def socialAuthenticate(provider: String) = Action {
    Ok("{ success: true }").as(JSON)
  }

  def signOut = Action {
    Ok("{ success: true }").as(JSON)
  }
}
