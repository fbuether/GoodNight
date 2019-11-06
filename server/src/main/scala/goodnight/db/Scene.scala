
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.model


class Scene(tag: Tag) extends Table[model.Scene](tag, "scene") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[UUID]("story")
  def raw = column[String]("raw")
  def title = column[String]("title")
  def image = column[String]("image")
  def location = column[Option[UUID]]("location")
  def text = column[String]("text")
  def mandatory = column[Boolean]("mandatory")
  def urlname = column[String]("urlname")

  def * = ((id, story, raw, title, urlname, image, location, text, mandatory) <>
    (model.Scene.tupled, model.Scene.unapply))

  def storyFk = foreignKey("scene_fk_story_story", story, Story())(_.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
  def locationFk = foreignKey("scene_fk_location_location", location,
    Location())(_.id.?,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Scene {
  def apply() = TableQuery[Scene]
}
