
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.exceptions.SilhouetteException
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.InvalidPasswordException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import java.util.UUID
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import upickle.default.macroRW

import goodnight.db
import goodnight.common.Serialise._
import goodnight.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database



class SignIn(components: ControllerComponents,
  database: Database,
  silhouette: AuthService,
  credentialsProvider: CredentialsProvider)(
  implicit ec: ExecutionContext)
    extends Controller(components) {


  case class SignInData(identity: String, password: String)
  implicit val serialise_signInData: Serialisable[SignInData] = macroRW


  def authenticate = silhouette.UnsecuredAction.async(
    parseFromJson[SignInData])({ request =>
      val signInData = request.body
      val credentials = Credentials(signInData.identity, signInData.password)
      credentialsProvider.authenticate(credentials).flatMap({ login =>
        silhouette.env.identityService.retrieve(login).flatMap({
          case Some(user) =>
            val authServ = silhouette.env.authenticatorService
            authServ.create(login)(request).flatMap({ authenticator =>
              authServ.init(authenticator)(request) }).flatMap({ ident =>
                val reply = Accepted(user.user.model)
                authServ.embed(ident, reply)(request)
              })
          case None =>
            Future.successful(Unauthorized)
        })
      }).recoverWith({ case (e: SilhouetteException) =>
        Future.successful(Unauthorized(ujson.Obj(
          "success" -> false,
          "error" -> e.getMessage())))
      })
    })

  def signOut = silhouette.SecuredAction.async { request =>
    val authServ = silhouette.env.authenticatorService
    authServ.retrieve(request).flatMap({
      case Some(authenticator) =>
        println("discarding authenticator " + authenticator)
        authServ.discard(authenticator, Ok)(request)
      case None =>
        Future.successful(Gone)
    })
  }
}
