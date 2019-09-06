
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

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import goodnight.server.Controller

import goodnight.model.User


class SignUp(components: ControllerComponents,
  silhouette: Silhouette[JwtEnvironment])(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  case class SignUpData(identity: String, password: String)
  implicit val signUpDataReads: Reads[SignUpData] = (
    (JsPath \ "identity").read[String] and
      (JsPath \ "password").read[String])(SignUpData.apply _)

  def doSignUp = silhouette.UnsecuredAction.async(parse.json) {
    request: Request[JsValue] => {
      request.body.validate[SignUpData] match {
        case JsSuccess(signUpData, _) =>
          val login = LoginInfo(CredentialsProvider.ID, signUpData.identity)
          silhouette.env.identityService.retrieve(login).
            flatMap {
              case Some(u) =>
                val authServ = silhouette.env.authenticatorService
                authServ.create(login)(request).flatMap({ authenticator =>
                  authServ.init(authenticator)(request) }).flatMap({ v =>
                    authServ.embed(v, Ok("found user " + u.id))(request)
                  })

              case None =>
                Future.successful(Ok("no user."))
            }
        case JsError(errors) =>
          val errorMessage = Json.obj(
            "success" -> false,
            "errors" -> errors.map({ case (p,ves) =>
              Json.obj(
                "path" -> p.toString,
                "errors" -> ves.map({ case JsonValidationError(a) =>
                  a}))}))
          Future.successful(
            BadRequest(errorMessage)
          )
      }}}


}
