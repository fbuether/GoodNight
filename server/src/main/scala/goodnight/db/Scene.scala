
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


//   private def ofStoryQuery(storyUrlname: Rep[String],
//     sceneUrlname: Rep[String]) =
//     apply().
//       join(Story().filter(_.urlname === storyUrlname)).on(_.story === _.id).
//       map(_._1).
//       filter(_.urlname === sceneUrlname).
//       take(1)
//   private val ofStoryCompiled = Compiled(ofStoryQuery _)
//   def ofStory(storyUrlname: String, sceneUrlname: String):
//       DBIO[Option[model.Scene]] =
//     ofStoryCompiled(storyUrlname, sceneUrlname).result.headOption


//   private def allOfStoryQuery(storyUrlname: Rep[String]) =
//     apply().
//       join(Story().filter(_.urlname === storyUrlname)).on(_.story === _.id).
//       map(_._1)
//   private val allOfStoryCompiled = Compiled(allOfStoryQuery _)
//   def allOfStory(storyUrlname: String): DBIO[Seq[model.Scene]] =
//     allOfStoryCompiled(storyUrlname).result


//   private def atLocationQuery(storyId: Rep[UUID],
//     playerLocation: Option[UUID]) =
//     apply().
//       filter(_.story === storyId).
//       filterOpt(playerLocation)(_.location === _).
//       sortBy(_.title)
//   // cannot precompile due to un-rep-value
//   // private val atLocationCompiled = Compiled(atLocationQuery _)
//   def atLocation(storyId: UUID, locationId: Option[UUID]):
//       DBIO[Seq[model.Scene]] =
//     atLocationQuery(storyId, locationId).result


//   private def byIdQuery(id: Rep[UUID]) =
//     apply().
//       filter(_.id === id)
//   private def byIdCompiled = Compiled(byIdQuery _)


//   def update(id: UUID, newScene: model.Scene): DBIO[Int] =
//     byIdCompiled(id).
//       update(newScene.copy(id = id))
}
