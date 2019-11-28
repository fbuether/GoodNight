
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase
import goodnight.model


class Location(tag: Tag) extends Table[model.Location](tag, "location") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[UUID]("story")
  def name = column[String]("name")

  def * = ((id, story, name) <>
    (model.Location.tupled, model.Location.unapply))

  def storyFk = foreignKey("location_fk_story_story", story,
    Story())(_.id,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Location extends TableQueryBase[model.Location, Location](
  new Location(_)) {
}
