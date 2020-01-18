
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableBase
import goodnight.server.TableQueryBase


class Login(tag: Tag) extends TableBase[model.Login](tag, "login") {
  def id = column[UUID]("id", O.PrimaryKey)
  def user = column[UUID]("user_id")
  def providerID = column[String]("provider_id")
  def providerKey = column[String]("provider_key")

  def * = (id, user, providerID, providerKey).
    mapTo[model.Login]

  def userFk = foreignKey("login_fk_users_user", user, User())(_.id,
    onUpdate=ForeignKeyAction.Cascade,
    onDelete=ForeignKeyAction.Cascade)
}


object Login extends TableQueryBase[model.Login, Login](new Login(_)) {
}
