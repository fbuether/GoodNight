
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase


class Quality(val tag: Tag) extends Table[model.Quality](tag, "quality") {
  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[String]("story")
  def raw = column[String]("raw")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  // def sort = ?
  def image = column[String]("image")
  def description = column[String]("description")

  def * = (id, story, raw, name, urlname, //sort,
    image, description).
    mapTo[model.Quality]

  def storyFk = foreignKey("quality_fk_story_story_urlname", story,
    Story())(_.urlname,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
}


object Quality extends TableQueryBase[model.Quality, Quality](new Quality(_)) {
  private val allOfStoryQuery = Compiled((story: Rep[String]) =>
    apply().
      filter(_.story === story))
  def allOfStory(story: String): DBIO[Seq[model.Quality]] =
    allOfStoryQuery(story).result
}
