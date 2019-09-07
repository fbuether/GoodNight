
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
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher

import goodnight.server.PostgresProfile.Database
// import goodnight.server.
import goodnight.server.Controller

import goodnight.model.{ User, UserTable }
import goodnight.model.{ Login, LoginTable }


class SignUp(components: ControllerComponents,
  db: Database,
  silhouette: Silhouette[JwtEnvironment])(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  private val passwordHasher = new BCryptSha256PasswordHasher()

  case class SignUpData(identity: String, username: String, password: String)
  implicit val signUpDataReads: Reads[SignUpData] = (
    (JsPath \ "identity").read[String] and
      (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String])(SignUpData.apply _)


  def withJsonAs[A](innerAction: ((Request[JsValue], A) => Future[Result]))(
    request: Request[JsValue])(implicit reads: Reads[A]): Future[Result] = {
    request.body.validate[A] match {
      case JsSuccess(data, _) => innerAction(request, data)
      case JsError(errors) =>
          val errorMessage = Json.obj(
            "success" -> false,
            "errors" -> errors.map({ case (p,ves) =>
              Json.obj(
                "path" -> p.toString,
                "errors" -> ves.map({ case JsonValidationError(a) =>
                  a}))}))
          Future.successful(
            BadRequest(errorMessage))
    }
  }


  def doSignUp = silhouette.UnsecuredAction.async(parse.json)(
    withJsonAs((request: Request[JsValue], signUpData: SignUpData) => {
      val login = LoginInfo(CredentialsProvider.ID, signUpData.identity)
      silhouette.env.identityService.retrieve(login).flatMap({
        case Some(u) =>
          Future.successful(Forbidden(Json.obj(
            "success" -> false,
            "errors" -> "Email address is already registered.")))

        case None =>
          // this user has not been registered before, create a new one.
          val authServ = silhouette.env.authenticatorService
          val authInfo = passwordHasher.hash(signUpData.password)
          val userId = UUID.randomUUID()

          authServ.create(login)(request).flatMap({ authenticator =>
            authServ.init(authenticator)(request) }).flatMap({ jwt =>
              db.run(UserTable.users.insert(User(userId, signUpData.username)).
                andThen(LoginTable.logins.insert(Login(UUID.randomUUID(),
                  userId, login.providerID, login.providerKey)))).
                flatMap({ _ =>
                  authServ.embed(jwt, Created)(request)
                })
            })
      })
    }))
}
