
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.model


class Player(tag: Tag) extends Table[model.Player](tag, "player") {
  def id = column[UUID]("id", O.PrimaryKey)
  def user = column[UUID]("user_id")
  def story = column[UUID]("story")
  def name = column[String]("player_name")
  def location = column[Option[UUID]]("location")

  def * = ((id, user, story, name, location) <>
    (model.Player.tupled, model.Player.unapply))

  def userFk = foreignKey("player_fk_users_user_id", user, User())(_.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
  def storyFk = foreignKey("player_fk_story_story", story, Story())(_.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
  def locationFk = foreignKey("player_fk_location_location", location,
    Location())(_.id.?,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Player {
  def apply() = TableQuery[Player]

  def of(user: UUID, story: UUID) =
    apply().filter(player => player.user === user && player.story === story).
      take(1).result.headOption
}
