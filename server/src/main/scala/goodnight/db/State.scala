
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableBase
import goodnight.server.TableQueryBase


class State(val tag: Tag) extends TableBase[model.State](tag, "state") {
  def id = column[UUID]("id", O.PrimaryKey)
  def user = column[String]("user")
  def story = column[String]("story")
  def quality = column[String]("quality")
  def value = column[String]("value")

  def * = (id, user, story, quality, value).
  mapTo[model.State]

  def playerFk = foreignKey("state_fk_user_story_player_user_story",
    (user, story),
    Player())((player => (player.user, player.story)),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  def qualityFk = foreignKey("state_fk_story_quality_quality_story_name",
    (story, quality), Quality())(quality => (quality.story, quality.name),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object State extends TableQueryBase[model.State, State](new State(_)) {
  private val ofPlayerQuery = Compiled((user: Rep[String],
    story: Rep[String]) =>
    apply().
      filter(state => state.user === user && state.story === story).
      join(Quality()).on((state, quality) =>
        state.story === quality.story &&
          state.quality === quality.urlname))

  def ofPlayer(user: String, story: String):
      DBIO[Seq[(model.State, model.Quality)]] =
    ofPlayerQuery(user, story).result
}
