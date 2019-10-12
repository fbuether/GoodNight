
package goodnight.api

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc._
import play.api.libs.json.{ JsValue, Json, Reads, JsPath }

import com.mohiva.play.silhouette.api.Silhouette

import goodnight.server.Router
import goodnight.api.authentication.JwtEnvironment
import goodnight.api.authentication.UserService
import goodnight.server.Controller

import goodnight.model.{ User, UserTable }
import goodnight.model.{ Login, LoginTable }

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile.Database
import play.api.db.slick.DbName

import goodnight.common.api.User._


class Profile(
  components: ControllerComponents,
  db: Database,
  silhouette: Silhouette[JwtEnvironment])(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def show(user: String) = silhouette.UserAwareAction.async { request =>
    println(s"requesting $user for ${request.identity}")

    request.identity match {
      case Some(ident) if ident.user.name == user =>
        Future.successful(Ok(Json.toJson(ident.user)))
      case _ =>
        val query = UserTable().filter(_.name === user).result
        db.run(query).map(u =>
          Ok(Json.toJson(u)))
    }
  }
}
