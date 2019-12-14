
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase


class BearerToken(tag: Tag) extends Table[model.BearerToken](
  tag, "bearer_token") {
  def id = column[String]("id", O.PrimaryKey)
  def provider = column[String]("provider")
  def key = column[String]("key")
  def lastUsed = column[Long]("last_used")
  def expiration = column[Long]("expiration")
  def timeout = column[Option[Long]]("timeout")

  def * = ((id, provider, key, lastUsed, expiration, timeout) <>
    (model.BearerToken.tupled, model.BearerToken.unapply))
}


object BearerToken {
  def apply() = TableQuery[BearerToken]

  def insert(obj: model.BearerToken): DBIO[model.BearerToken] =
    apply().
      returning(apply()) += obj
}
