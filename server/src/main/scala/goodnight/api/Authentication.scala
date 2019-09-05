
package goodnight.api

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService

import goodnight.model.User


trait JWTEnv extends Env {
  type I = User
  type A = JWTAuthenticator
}



class UserService(implicit ec: ExecutionContext) extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    Future { None }
}


class Authentication(components: ControllerComponents,
  silhouette: Silhouette[JWTEnv])
    extends BaseController {

  def controllerComponents: ControllerComponents =
    components


  def doSignUp = Action {
    Ok("{ success: true }").as(JSON)
  }

  def confirmSignUp(token: String) = Action {
    Ok("{ success: true }").as(JSON)
  }

  def doRequestResetPassword = Action {
    Ok("{ success: true }").as(JSON)
  }

  def doResetPassword(token: String) = Action {
    Ok("{ success: true }").as(JSON)
  }

  def authenticate = silhouette.UnsecuredAction {
    Ok("{ success: true }").as(JSON)
  }

  def socialAuthenticate(provider: String) = Action {
    Ok("{ success: true }").as(JSON)
  }

  def signOut = Action {
    Ok("{ success: true }").as(JSON)
  }
}
