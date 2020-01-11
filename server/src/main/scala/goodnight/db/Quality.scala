
package goodnight.db

import java.util.UUID

import goodnight.server.PostgresProfile.api._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase





class Quality(val tag: Tag) extends Table[model.Quality](tag, "quality") {

  implicit private def sortColumnType =
    MappedColumnType.base[model.Sort, String]({
      case model.Sort.Bool => "bool"
      case model.Sort.Integer => "int"
      case _ => "unknown"
    }, {
      case "int" => model.Sort.Integer
      case _ => model.Sort.Bool
    })


  def id = column[UUID]("id", O.PrimaryKey)
  def story = column[String]("story")
  def raw = column[String]("raw")
  def name = column[String]("name")
  def urlname = column[String]("urlname")
  def sort = column[model.Sort]("sort")
  def image = column[String]("image")
  def description = column[String]("description")

  def * = (id, story, raw, name, urlname, sort,
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
