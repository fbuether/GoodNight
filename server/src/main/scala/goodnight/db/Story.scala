
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase
import goodnight.model


class Story(tag: Tag) extends Table[model.Story](tag, "story") {
  def id = column[UUID]("id", O.PrimaryKey)
  def creator = column[UUID]("creator")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  def image = column[String]("image")
  def description = column[String]("description")
  def startLocation = column[Option[UUID]]("start_location")

  def * = ((id, creator, name, urlname, image, description, startLocation) <>
    (model.Story.tupled, model.Story.unapply))

  def userFk = foreignKey("story_fk_users_user", creator, User())(_.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object Story extends TableQueryBase[model.Story, Story](new Story(_)) {
  private def ofUrlnameQuery(urlname: Rep[String]) = apply().
    filter(_.urlname === urlname).
    take(1)
  private val ofUrlnameCompiled = Compiled(ofUrlnameQuery _)
  def ofUrlname(urlname: String): DBIO[Option[model.Story]] =
    ofUrlnameCompiled(urlname).result.headOption

  private val allPublicCompiled = Compiled(apply())
  def allPublic: DBIO[Seq[model.Story]] =
    allPublicCompiled.result

  private def ofUserQuery(userId: Rep[UUID]) = apply().
    filter(_.creator === userId)
  private val ofUserCompiled = Compiled(ofUserQuery _)
  def ofUser(userId: UUID): DBIO[Seq[model.Story]] =
    ofUserCompiled(userId).result
}
