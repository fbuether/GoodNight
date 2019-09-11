
package goodnight.api.authentication

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
import slick.jdbc.PostgresProfile.api._

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.InvalidPasswordException
import com.mohiva.play.silhouette.api.exceptions.SilhouetteException

import goodnight.server.PostgresProfile.Database
import goodnight.server.Controller

import goodnight.model.{ User, UserTable }
import goodnight.model.{ Login, LoginTable }



class SignIn(components: ControllerComponents,
  db: Database,
  silhouette: Silhouette[JwtEnvironment],
  credentialsProvider: CredentialsProvider)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  case class SignInData(identity: String, password: String)
  implicit val signInDataReads: Reads[SignInData] = (
    (JsPath \ "identity").read[String] and
      (JsPath \ "password").read[String])(SignInData.apply _)


  def authenticate = silhouette.UnsecuredAction.async(parse.json)(
    withJsonAs((request: Request[JsValue], signInData: SignInData) => {

      val credentials = Credentials(signInData.identity, signInData.password)
      credentialsProvider.authenticate(credentials).flatMap({ login =>
        silhouette.env.identityService.retrieve(login).flatMap({
          case Some(user) =>
            val authServ = silhouette.env.authenticatorService
            authServ.create(login)(request).flatMap({ authenticator =>
              authServ.init(authenticator)(request) }).flatMap({ ident =>
                authServ.embed(ident, Accepted)(request)
              })
          case None =>
            Future.successful(Unauthorized)
        })
      }).recoverWith({ case (e: SilhouetteException) =>
        Future.successful(Unauthorized(Json.obj(
          "success" -> false,
          "error" -> e.getMessage())))
      })
    }))

  def socialAuthenticate(provider: String) =
    TODO
}
