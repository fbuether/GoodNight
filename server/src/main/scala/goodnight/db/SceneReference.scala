
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableBase
import goodnight.server.TableQueryBase



// Scene references store all scenes that a single scene references, either
// by `next` or `include`.

class SceneReference(tag: Tag) extends Table[
  (UUID, String, String, Int, String)](tag, "sceneref") {

  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[String]("story")
  def from = column[String]("from")
  def kind = column[Int]("kind") // 0 = next, 1 = include
  def to = column[String]("to")

  def * = (id, story, from, kind, to)

  def storyFk = foreignKey("sceneref_fk_story_story_urlname", story,
    Story())(_.urlname,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)

  def fromSceneFk = foreignKey("sceneref_fk_story_from_scene_story_urlname",
    (story, from), Scene())((scene => (scene.story, scene.urlname)),
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object SceneReference {
  private def apply() = TableQuery[SceneReference]

  private val allReferencesAsScenesQuery = Compiled((story: Rep[String],
    scene: Rep[String]) =>
    apply().
      filter(ref => ref.story === story && ref.from === scene).
      joinLeft(Scene()).on(_.to === _.urlname).
      map({ case (r,s) => (r.to, s) }))
  def allReferencesAsScenes(storyUrlname: String, sceneUrlname: String):
      DBIO[Seq[(String, Option[model.Scene])]] =
    allReferencesAsScenesQuery(storyUrlname, sceneUrlname).result


  private val prevAsStringsQuery = Compiled((story: Rep[String],
    scene: Rep[String]) =>
    apply().
      filter(ref => ref.story === story && ref.kind === 0 && ref.to === scene).
      map(_.from))
  def prevAsStrings(storyUrlname: String, sceneUrlname: String):
      DBIO[Seq[String]] =
    prevAsStringsQuery(storyUrlname, sceneUrlname).result

  private val nextAsStringsQuery = Compiled((story: Rep[String],
    scene: Rep[String]) =>
    apply().
      filter(ref => ref.story === story && ref.kind === 0 &&
        ref.from === scene).
      map(_.to))
  def nextAsStrings(storyUrlname: String, sceneUrlname: String):
      DBIO[Seq[String]] =
    nextAsStringsQuery(storyUrlname, sceneUrlname).result


  def write(storyUrlname: String, sceneUrlname: String, references:
      Seq[(String, String)]): DBIO[Option[Int]] =
    apply().
      filter(ref => ref.story === storyUrlname && ref.from === sceneUrlname).
      delete.
      andThen(
        apply() ++= references.map(ref =>
        (UUID.randomUUID(), storyUrlname,
          sceneUrlname,
          (if (ref._1 == "include") 1 else 0),
          ref._2)))
}
