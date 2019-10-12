
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table

import goodnight.model

import com.mohiva.play.silhouette.api.Identity
// import com.mohiva.play.silhouette.api.LoginInfo
// import com.mohiva.play.silhouette.api.Authenticator
// import com.mohiva.play.silhouette.api.Authorization



// stores association from way of login to account.
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

  def userFk = foreignKey("login_fk_users_user", user, UserTable())(
    _.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
}

// implicit class LoginTableCreator(login: Login)

object LoginTable {
  def apply() = TableQuery[LoginTable]
}


// stores verification information for a specific login.
case class LoginAuth(
  id: UUID,
  providerID: String,
  providerKey: String,
  hasher: String,
  password: String,
  salt: Option[String]
)

class LoginAuthTable(tag: Tag) extends Table[LoginAuth](tag, "login_auth") {
  def id = column[UUID]("id", O.PrimaryKey)
  def providerID = column[String]("provider_id")
  def providerKey = column[String]("provider_key")
  def hasher = column[String]("hasher")
  def password = column[String]("password")
  def salt = column[Option[String]]("salt")
  def * = ((id, providerID, providerKey, hasher, password, salt) <>
    (LoginAuth.tupled, LoginAuth.unapply))
}

object LoginAuthTable {
  def apply() = TableQuery[LoginAuthTable]
}


class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")

  def * = ((id, name) <>
    (User.tupled, User.unapply))
}

object UserTable {
  def apply() = TableQuery[UserTable]
}


// case class IsAuthorised() extends Authorization[User, Authenticator] {
//   override def isAuthorized[B] ...
