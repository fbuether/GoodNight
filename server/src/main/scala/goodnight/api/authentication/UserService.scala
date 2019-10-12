
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction

import goodnight.model.{ Login, LoginTable }
import goodnight.model.{ User, UserTable }
import goodnight.server.PostgresProfile.Database


class UserService(
  db: Database)(
  implicit ec: ExecutionContext)
    extends IdentityService[User] {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val getUserFromLogin = Compiled(
      LoginTable().
        join(UserTable()).on(_.user === _.id).
        filter({ case (l,u) => l.providerID === loginInfo.providerID &&
          l.providerKey === loginInfo.providerKey }).
        map({ case (l,u) => u }).
        take(1)).result.headOption
    db.run(getUserFromLogin)
  }
}
