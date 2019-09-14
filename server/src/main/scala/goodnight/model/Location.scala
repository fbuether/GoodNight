
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table



case class Location(
  id: UUID,
  story: UUID,
  name: String
)


class LocationTable(tag: Tag) extends Table[Location](tag, "location") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[UUID]("story")
  def name = column[String]("name")

  def * = ((id, story, name) <>
    (Location.tupled, Location.unapply))

  def storyFk = foreignKey("location_fk_story_story", story, StoryTable())(
    _.id, onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object LocationTable {
  def apply() = TableQuery[LocationTable]
}
