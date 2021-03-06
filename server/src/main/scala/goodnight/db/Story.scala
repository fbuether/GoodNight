
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableBase
import goodnight.server.TableQueryBase


class Story(tag: Tag) extends TableBase[model.Story](tag, "story") {
  def id = column[UUID]("id", O.PrimaryKey)
  def creator = column[String]("creator")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  def image = column[String]("image")
  def description = column[String]("description")
  def public = column[Boolean]("public")

  def * = (id, creator, name, urlname, image, description, public).
    mapTo[model.Story]

  def userFk = foreignKey("story_fk_creator_user_name", creator, User())(_.name,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object Story extends TableQueryBase[model.Story, Story](new Story(_)) {
  private val ofUrlnameQuery = Compiled((urlname: Rep[String]) =>
    apply().
      filter(_.urlname === urlname).
      take(1))
  def ofUrlname(urlname: String): DBIO[Option[model.Story]] =
    ofUrlnameQuery(urlname).result.headOption

  private val allQuery = Compiled(apply())
  def all: DBIO[Seq[model.Story]] =
    allQuery.result

  private val allPublicQuery = Compiled(apply().
    filter(_.public))
  def allPublic: DBIO[Seq[model.Story]] =
    allPublicQuery.result

  private val ofUserQuery = Compiled((userName: Rep[String]) =>
    apply().
      filter(_.creator === userName))
  def ofUser(userName: String): DBIO[Seq[model.Story]] =
    ofUserQuery(userName).result
}
