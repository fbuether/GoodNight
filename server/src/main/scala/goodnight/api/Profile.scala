
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
import goodnight.common.Serialise._
import goodnight.model.Login
import goodnight.model.User
import goodnight.db
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.server.Router


class Profile(
  components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def show(user: String) = auth.UserAwareAction.async { request =>
    println(s"requesting $user for ${request.identity}")

    request.identity match {
      case Some(ident) if ident.user.name == user =>
        Future.successful(Ok(write(ident.user)))
      case _ =>
        val query = db.User().filter(_.name === user).result
        database.run(query).map(u =>
          Ok(write(u)))
    }
  }

  def showSelf = auth.SecuredAction({ request =>
    Ok(request.identity.user)
  })
}
