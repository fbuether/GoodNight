
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase


class Scene(tag: Tag) extends Table[model.Scene](tag, "scene") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[String]("story")
  def raw = column[String]("raw")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  def text = column[String]("text")

  def * = ((id, story, raw, name, urlname, text) <>
    (model.Scene.tupled, model.Scene.unapply))

  def storyFk = foreignKey("scene_fk_story_story_urlname", story,
    Story())(_.urlname,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object Scene extends TableQueryBase[model.Scene, Scene](new Scene(_)) {
  private val defaultOfStoryQuery = Compiled((story: Rep[String]) =>
    apply().
      filter(_.story === story).
      take(1))
  def defaultOfStory(story: String): DBIO[Option[model.Scene]] =
    defaultOfStoryQuery(story).result.headOption

  private val namedQuery = Compiled((story: Rep[String],
    urlname: Rep[String]) =>
    apply().
      filter(scene => scene.story === story && scene.urlname === urlname).
      take(1))
  def named(story: String, scene: String): DBIO[Option[model.Scene]] =
    namedQuery(story, scene).result.headOption


  private val namedListQuery = Compiled((story: Rep[String],
    scenes: Rep[List[String]]) =>
    apply().
      filter(scene => scene.story === story &&
        scene.urlname === scenes.any))
  def namedList(story: String, scenes: List[String]): DBIO[Seq[model.Scene]] =
    namedListQuery((story, scenes)).result


  private val allOfStoryQuery = Compiled((story: Rep[String]) =>
    apply().
      filter(_.story === story))
  def allOfStory(story: String): DBIO[Seq[model.Scene]] =
    allOfStoryQuery(story).result
}
