
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase
import goodnight.model


class Scene(tag: Tag) extends Table[model.Scene](tag, "scene") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[UUID]("story")
  def raw = column[String]("raw")
  def title = column[String]("title")
  def urlname = column[String]("urlname")
  def text = column[String]("text")
  def location = column[Option[UUID]]("location")
  def mandatory = column[Boolean]("mandatory")

  def * = ((id, story, raw, title, urlname, text, location, mandatory) <>
    (model.Scene.tupled, model.Scene.unapply))

  def storyFk = foreignKey("scene_fk_story_story", story, Story())(_.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
  def locationFk = foreignKey("scene_fk_location_location", location,
    Location())(_.id.?,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Scene extends TableQueryBase[model.Scene, Scene](new Scene(_)) {
  private def ofStoryQuery(storyUrlname: Rep[String],
    sceneUrlname: Rep[String]) =
    apply().
      join(Story().filter(_.urlname === storyUrlname)).on(_.story === _.id).
      map(_._1).
      filter(_.urlname === sceneUrlname).
      take(1)
  private val ofStoryCompiled = Compiled(ofStoryQuery _)
  def ofStory(storyUrlname: String, sceneUrlname: String):
      DBIO[Option[model.Scene]] =
    ofStoryCompiled(storyUrlname, sceneUrlname).result.headOption


  private def allOfStoryQuery(storyUrlname: Rep[String]) =
    apply().
      join(Story().filter(_.urlname === storyUrlname)).on(_.story === _.id).
      map(_._1)
  private val allOfStoryCompiled = Compiled(allOfStoryQuery _)
  def allOfStory(storyUrlname: String): DBIO[Seq[model.Scene]] =
    allOfStoryCompiled(storyUrlname).result


  private def forPlayerQuery(storyId: Rep[UUID],
    playerLocation: Rep[Option[UUID]]) =
    apply().
      filter(scene => scene.location === playerLocation &&
        scene.story === storyId).
      sortBy(_.title)
  private val forPlayerCompiled = Compiled(forPlayerQuery _)
  def forPlayer(storyId: UUID, playerLocationId: Option[UUID]):
      DBIO[Seq[model.Scene]] =
    forPlayerCompiled(storyId, playerLocationId).result


  private def byIdQuery(id: Rep[UUID]) =
    apply().
      filter(_.id === id).
      take(1)
  private def byIdCompiled = Compiled(byIdQuery _)


  def update(id: UUID, newScene: model.Scene): DBIO[Int] =
    byIdCompiled(id).
      update(newScene.copy(id = id))
}
