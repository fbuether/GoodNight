
package goodnight.api.authentication

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

// import play.api.mvc.Request
// import play.api.mvc.AnyContent
// import play.api.mvc.BaseController
// import play.api.mvc.ControllerComponents
// import play.api.libs.json.JsValue
// import play.api.libs.json.{ Json, Reads, JsPath, JsSuccess, JsError, JsonValidationError }
// import play.api.libs.functional.syntax._

// import com.mohiva.play.silhouette.api.Silhouette
// import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
// import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService
// import com.mohiva.play.silhouette.impl.providers.CredentialsProvider


import goodnight.model.User


class UserService(
  implicit ec: ExecutionContext)
    extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    Future {
      Some(User(UUID.randomUUID(), "username"))
      }
}
