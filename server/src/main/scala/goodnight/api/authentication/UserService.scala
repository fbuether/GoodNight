
package goodnight.api.authentication

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

// import play.api.mvc.Request
// import play.api.mvc.AnyContent
// import play.api.mvc.BaseController
// import play.api.mvc.ControllerComponents
// import play.api.libs.json.JsValue
// import play.api.libs.json.{ Json, Reads, JsPath, JsSuccess, JsError, JsonValidationError }
// import play.api.libs.functional.syntax._

// import com.mohiva.play.silhouette.api.Silhouette
// import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.api.LoginInfo
// import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.services.IdentityService
// import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import slick.sql.SqlAction
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile.Database
import goodnight.model.{ User, UserTable }
import goodnight.model.{ Login, LoginTable }



class UserService(
  db: Database)(
  implicit ec: ExecutionContext)
    extends IdentityService[User] {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val getUserFromLogin = Compiled(
      LoginTable().join(UserTable()).on(_.user === _.id).
        filter({ case (l,u) => l.providerID === loginInfo.providerID &&
          l.providerKey === loginInfo.providerKey }).
        map({ case (l,u) => u }).
        take(1)).result.headOption
    db.run(getUserFromLogin)
  }


  // auth info repository?
}
