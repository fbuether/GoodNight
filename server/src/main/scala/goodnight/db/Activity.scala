
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableBase
import goodnight.server.TableQueryBase


class Activity(val tag: Tag) extends TableBase[model.Activity](
  tag, "activity") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[String]("story")
  def user = column[String]("user")
  def number = column[Int]("number")
  def scene = column[String]("scene")
  def random = column[List[Int]]("random")

  def * = (id, story, user, number, scene, random).
    mapTo[model.Activity]

  def storyFk = foreignKey("activity_fk_story_story_urlname", story,
    Story())(_.urlname,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  def userFk = foreignKey("activity_fk_user_user_name", user,
    User())(_.name, onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  def sceneFk = foreignKey("activity_fk_story_scene_scene_story_urlname",
    (story, scene), Scene())(scene => (scene.story, scene.urlname),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  def playerFk = foreignKey("activity_fk_user_story_player_user_story",
    (user, story), Player())(player => (player.user, player.story),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Activity extends TableQueryBase[model.Activity, Activity](
  new Activity(_)) {
  private val newestQuery = Compiled((story: Rep[String], user: Rep[String]) =>
    apply().
      filter(activity => activity.story === story && activity.user === user).
      sortBy(_.number.desc).
      take(1))
  def newest(story: String, user: String): DBIO[Option[model.Activity]] =
    newestQuery(story, user).result.headOption
}
