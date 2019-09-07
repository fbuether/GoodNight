
package goodnight.api

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import play.api.mvc._

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


class Profile(
  components: ControllerComponents,
  userService: UserService,
  db: Database,
  silhouette: Silhouette[JwtEnvironment])(
  implicit ec: ExecutionContext)
    extends Controller(components) {

  def getName(user: User) = user.name


  def show = silhouette.UserAwareAction.async { request =>
    request.identity match {
      case Some(id) => Future.successful(Ok("Hello " + id))
      case None =>

        val userId = UUID.randomUUID()
        val insertions =
        DBIO.seq(
          UserTable.users += User(userId, "myusername"),
          LoginTable.logins += Login(UUID.randomUUID(), userId,
            "somwhere", "somewhow")
        )

        db.run(insertions)


        val user = UserTable.users.result

        db.run(user).// map({
          map({ r =>
            Ok("Hello, unidentified person.\n" + r
              // getName(dbConfig.db)
            )
          })
    }
  }
}
