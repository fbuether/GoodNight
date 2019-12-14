
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase


class Player(val tag: Tag) extends Table[model.Player](tag, "player") {
  def id = column[UUID]("id", O.PrimaryKey)
  def user = column[String]("user")
  def story = column[String]("story")
  def name = column[String]("name")

  def * = ((id, user, story, name) <>
    (model.Player.tupled, model.Player.unapply))

  def userFk = foreignKey("player_fk_user_user_name", user, User())(_.name,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
  def storyFk = foreignKey("player_fk_story_story_urlname", story,
    Story())(_.urlname,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Player extends TableQueryBase[model.Player, Player](new Player(_)) {
  private val ofStoryQuery = Compiled((user: Rep[String], story: Rep[String]) =>
    apply().
      filter(player => player.user === user && player.story === story).
      take(1))
  def ofStory(user: String, story: String): DBIO[Option[model.Player]] =
    ofStoryQuery(user, story).result.headOption


  private val ofNameQuery = Compiled((name: Rep[String]) =>
    apply().
      filter(_.name === name).
      take(1))
  def ofName(name: String): DBIO[Option[model.Player]] =
    ofNameQuery(name).result.headOption
}
