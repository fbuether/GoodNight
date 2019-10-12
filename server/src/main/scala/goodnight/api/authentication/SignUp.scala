
package goodnight.api.authentication

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc.Request
import play.api.mvc.Result
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
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository

import goodnight.server.PostgresProfile.Database
import goodnight.server.Controller

import goodnight.model.{ User, UserTable }
import goodnight.model.{ Login, LoginTable }


class SignUp(components: ControllerComponents,
  db: Database,
  silhouette: Silhouette[JwtEnvironment],
  passwordRegistry: PasswordHasherRegistry,
  authInfoRepository: AuthInfoRepository)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  case class SignUpData(identity: String, username: String, password: String)
  implicit val signUpDataReads: Reads[SignUpData] = (
    (JsPath \ "identity").read[String] and
      (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String])(SignUpData.apply _)

  def doSignUp = silhouette.UnsecuredAction.async(parse.json)(
    withJsonAs((request: Request[JsValue], signUpData: SignUpData) => {
      // todo: check if signUpData.username as a username already exists.

      val login = LoginInfo(CredentialsProvider.ID, signUpData.identity)
      silhouette.env.identityService.retrieve(login).flatMap({
        case Some(u) =>
          Future.successful(Forbidden(Json.obj(
            "success" -> false,
            "errors" -> "Email address is already registered.")))

        case None =>
          // this user has not been registered before, create a new one.
          val authServ = silhouette.env.authenticatorService
          val authInfo = passwordRegistry.current.hash(signUpData.password)
          val userId = UUID.randomUUID()

          authServ.create(login)(request).flatMap({ authenticator =>
            authServ.init(authenticator)(request) }).flatMap({ ident =>
              db.run(UserTable().insert(User(userId, signUpData.username)).
                andThen(LoginTable().insert(Login(UUID.randomUUID(),
                  userId, login.providerID, login.providerKey)))).
                flatMap({ _ =>
                  authInfoRepository.add(login, authInfo).flatMap({ _ =>
                    authServ.embed(ident, Created)(request)
                  })
                })
            })
      })
    }))
}
