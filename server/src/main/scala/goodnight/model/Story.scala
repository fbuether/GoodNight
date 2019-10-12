
package goodnight.model

import java.util.UUID

import slick.jdbc.PostgresProfile.api._
import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table

import goodnight.model


class StoryTable(tag: Tag) extends Table[model.Story](tag, "story") {
  def id = column[UUID]("id", O.PrimaryKey)
  def creator = column[UUID]("creator")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  def image = column[String]("image")
  def description = column[String]("description")
  def startLocation = column[Option[UUID]]("start_location")

  def * = ((id, creator, name, urlname, image, description, startLocation) <>
    (model.Story.tupled, model.Story.unapply))

  def userFk = foreignKey("story_fk_users_user", creator, UserTable())(
    _.id, onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object StoryTable {
  def apply() = TableQuery[StoryTable]

  def filterCreator(name: String)(base: TableQuery[StoryTable]) = {
    base.join(UserTable().filter(_.name === name)).on(_.creator === _.id).
      map((su: (StoryTable, UserTable)) => su._1)
  }
}
