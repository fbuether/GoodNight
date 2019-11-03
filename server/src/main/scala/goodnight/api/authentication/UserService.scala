
package goodnight.api.authentication

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction

import goodnight.db
import goodnight.model.Login
import goodnight.model.User
import goodnight.server.PostgresProfile.Database


class UserService(
  database: Database)(
  implicit ec: ExecutionContext)
    extends IdentityService[Id] {

  def retrieve(loginInfo: LoginInfo): Future[Option[Id]] = {
    val getUserFromLogin = Compiled(
      db.Login().
        join(db.User()).on(_.user === _.id).
        filter({ case (l,u) => l.providerID === loginInfo.providerID &&
          l.providerKey === loginInfo.providerKey }).
        map({ case (l,u) => u }).
        take(1)).result.headOption.
      map(_.map(Id(_)))
    database.run(getUserFromLogin)
  }
}
