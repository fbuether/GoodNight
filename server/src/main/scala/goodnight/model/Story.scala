
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table



case class Story(
  id: UUID,
  creator: UUID,
  name: String,
  urlname: String,
  image: String,
  description: String,
  // theme: String,
  startLocation: Option[UUID]
)



class StoryTable(tag: Tag) extends Table[Story](tag, "story") {
  def id = column[UUID]("id", O.PrimaryKey)
  def creator = column[UUID]("creator")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  def image = column[String]("image")
  def description = column[String]("description")
  def startLocation = column[Option[UUID]]("start_location")

  def * = ((id, creator, name, urlname, image, description, startLocation) <>
    (Story.tupled, Story.unapply))

  def userFk = foreignKey("story_fk_users_user", creator, UserTable())(
    _.id, onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object StoryTable {
  def apply() = TableQuery[StoryTable]
}
