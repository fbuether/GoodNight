
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table



class SceneTable(tag: Tag) extends Table[Scene](tag, "scene") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[UUID]("story")
  def raw = column[String]("raw")
  def title = column[String]("title")
  def image = column[String]("image")
  def location = column[Option[UUID]]("location")
  def text = column[String]("text")
  def mandatory = column[Boolean]("mandatory")
  def urlname = column[String]("urlname")

  def * = ((id, story, raw, title, image, location, text, mandatory, urlname) <>
    (Scene.tupled, Scene.unapply))

  def storyFk = foreignKey("scene_fk_story_story", story, StoryTable())(
    _.id, onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
  def locationFk = foreignKey("scene_fk_location_location", location,
    LocationTable())(
    _.id.?, onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object SceneTable {
  def apply() = TableQuery[SceneTable]
}
