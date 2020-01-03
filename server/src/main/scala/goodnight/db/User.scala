
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase


class User(tag: Tag) extends Table[model.User](tag, "user") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")

  def * = (id, name).
    mapTo[model.User]
}


object User extends TableQueryBase[model.User, User](new User(_)) {
  private val ofNameQuery = Compiled((name: Rep[String]) =>
    apply().
      filter(_.name === name).
      take(1))
  def ofName(name: String) =
    ofNameQuery(name).result.headOption
}
