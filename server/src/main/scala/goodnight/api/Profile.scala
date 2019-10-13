
package goodnight.api

import java.util.UUID
import play.api.db.slick.DbName
import play.api.libs.json.{ JsValue, Json, Reads, JsPath }
import play.api.mvc._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.UserService
import goodnight.common.api.User._
import goodnight.model.{ Login, LoginTable }
import goodnight.model.{ User, UserTable }
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.server.Router


class Profile(
  components: ControllerComponents,
  db: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def show(user: String) = auth.UserAwareAction.async { request =>
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
