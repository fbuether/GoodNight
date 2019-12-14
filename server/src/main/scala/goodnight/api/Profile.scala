
package goodnight.api

import java.util.UUID
import play.api.db.slick.DbName
import play.api.mvc._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

import goodnight.api.authentication.AuthService
import goodnight.api.authentication.UserService
import goodnight.common.Serialise._
import goodnight.db
import goodnight.db.model
import goodnight.server.Controller
import goodnight.server.PostgresProfile.Database
import goodnight.server.Router


class Profile(
  components: ControllerComponents,
  database: Database,
  auth: AuthService)(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def show(userName: String) =
    auth.UserAwareAction.async(request =>
      database.run(
        db.User.ofName(userName).
          map(_.map(user => Ok(user.model)).
            getOrElse(NotFound))))

  def showSelf =
    auth.SecuredAction(request =>
      Ok(request.identity.user.model))
}
