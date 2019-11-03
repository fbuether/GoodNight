
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.model


class LoginAuth(tag: Tag) extends Table[model.LoginAuth](tag, "login_auth") {
  def id = column[UUID]("id", O.PrimaryKey)
  def providerID = column[String]("provider_id")
  def providerKey = column[String]("provider_key")
  def hasher = column[String]("hasher")
  def password = column[String]("password")
  def salt = column[Option[String]]("salt")
  def * = ((id, providerID, providerKey, hasher, password, salt) <>
    (model.LoginAuth.tupled, model.LoginAuth.unapply))
}

object LoginAuth {
  def apply() = TableQuery[LoginAuth]
}