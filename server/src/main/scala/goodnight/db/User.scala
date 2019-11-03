
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.model


class User(tag: Tag) extends Table[model.User](tag, "users") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")

  def * = ((id, name) <>
    (model.User.tupled, model.User.unapply))
}


object User {
  def apply() = TableQuery[User]
}
