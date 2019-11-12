
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import java.util.UUID
import play.api.libs.functional.syntax._
import play.api.libs.json.JsValue
import play.api.libs.json.{ Json, Reads, JsPath, JsSuccess, JsError, JsonValidationError }
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.common.Serialise._
import goodnight.db
import goodnight.model.Login
import goodnight.model.User
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database


class SignUp(components: ControllerComponents,
  database: Database,
  silhouette: AuthService,
  passwordRegistry: PasswordHasherRegistry,
  authInfoRepository: AuthInfoRepository)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  case class SignUpData(identity: String, username: String, password: String)
  implicit val serialise_signUpData: Serialisable[SignUpData] = macroRW

  def doSignUp = silhouette.UnsecuredAction.async(
    parseFromJson[SignUpData])({ request =>
      val signUpData = request.body
      // todo: check if signUpData.username as a username already exists.

      val login = LoginInfo(CredentialsProvider.ID, signUpData.identity)
      silhouette.env.identityService.retrieve(login).flatMap({
        case Some(u) =>
          Future.successful(Forbidden(write(
            "success" -> false,
            "errors" -> "Email address is already registered.")))

        case None =>
          // this user has not been registered before, create a new one.
          val authServ = silhouette.env.authenticatorService
          val authInfo = passwordRegistry.current.hash(signUpData.password)
          val userId = UUID.randomUUID()

          authServ.create(login)(request).flatMap({ authenticator =>
            authServ.init(authenticator)(request) }).flatMap({ ident =>
              database.run(db.User().insert(User(userId, signUpData.username)).
                andThen(db.Login().insert(Login(UUID.randomUUID(),
                  userId, login.providerID, login.providerKey)))).
                flatMap({ _ =>
                  authInfoRepository.add(login, authInfo).flatMap({ _ =>
                    authServ.embed(ident, Created)(request)
                  })
                })
            })
      })
    })
}
