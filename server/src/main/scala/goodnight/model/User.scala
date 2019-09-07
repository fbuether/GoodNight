
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table

import com.mohiva.play.silhouette.api.Identity
// import com.mohiva.play.silhouette.api.LoginInfo
// import com.mohiva.play.silhouette.api.Authenticator
// import com.mohiva.play.silhouette.api.Authorization


case class Login(
  id: UUID,
  user: UUID,
  providerID: String,
  providerKey: String
)

class LoginTable(tag: Tag) extends Table[Login](tag, "login") {
  def id = column[UUID]("id", O.PrimaryKey)
  def user = column[UUID]("user_id")
  def providerID = column[String]("provider_id")
  def providerKey = column[String]("provider_key")
  def * = ((id, user, providerID, providerKey) <>
    (Login.tupled, Login.unapply))

  def userFk = foreignKey("login_fk_users_user", user, UserTable.users)(
    _.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
}

object LoginTable {
  val logins = TableQuery[LoginTable]
}



case class User(
  id: UUID,
  name: String,
  // login: Login

  // login_providerID: String,
  // login_providerKey: String

  // email: String,
  // password: String,
  // canCreateWorlds: Boolean,
  // cookie: Option[String],
  // staySignedIn: Boolean
) extends Identity


class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  // def login_providerID = column[String]("login_provider_id")
  // def login_providerKey = column[String]("login_provider_key")

  def * = ((id, name// , login_providerID, login_providerKey
  )
    <> (User.tupled, User.unapply))
}

object UserTable {
  val users = TableQuery[UserTable]
}


// case class IsAuthorised() extends Authorization[User, Authenticator] {
//   override def isAuthorized[B] ...
