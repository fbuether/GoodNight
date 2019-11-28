
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase
import goodnight.model


class Player(val tag: Tag) extends Table[model.Player](tag, "player") {
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


object Player extends TableQueryBase[model.Player, Player](new Player(_)) {
  private def ofStoryQuery(userId: Rep[UUID], storyId: Rep[UUID]) = apply().
    filter(player => player.user === userId && player.story === storyId).
    take(1)
  private val ofStoryCompiled = Compiled(ofStoryQuery _)
  def ofStory(userId: UUID, storyId: UUID): DBIO[Option[model.Player]] =
    ofStoryCompiled(userId, storyId).result.headOption
}
