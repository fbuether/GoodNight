
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Authenticator
import com.mohiva.play.silhouette.api.Authorization


case class User(
  id: UUID,
  name: String,
  // login: LoginInfo

  login_providerID: String,
  login_providerKey: String

  // email: String,
  // password: String,
  // canCreateWorlds: Boolean,
  // cookie: Option[String],
  // staySignedIn: Boolean
) extends Identity


class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def login_providerID = column[String]("login_provider_id")
  def login_providerKey = column[String]("login_provider_key")

  def * = ((id, name, login_providerID, login_providerKey)
    <> (User.tupled, User.unapply))
}

object UserTable {
  val users = TableQuery[UserTable]
}


// case class IsAuthorised() extends Authorization[User, Authenticator] {
//   override def isAuthorized[B] ...
