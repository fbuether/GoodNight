
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
import goodnight.server.TableQueryBase
import goodnight.model


class Choice(tag: Tag) extends Table[model.Choice](tag, "choice") {
  def id = column[UUID]("id", O.PrimaryKey)
  def scene = column[UUID]("scene")
  def pos = column[Int]("pos")
  def text = column[String]("text")

  def * = ((id, scene, pos, text) <>
    (model.Choice.tupled, model.Choice.unapply))

  def userFk = foreignKey("choice_fk_scene_scene", scene, Scene())(_.id,
    onUpdate = ForeignKeyAction.Cascade,
    onDelete = ForeignKeyAction.Cascade)
}


object Choice extends TableQueryBase[model.Choice, Choice](new Choice(_)) {
  private def updateTextQuery(sceneId: Rep[UUID], pos: Rep[Int]) =
    apply().
      filter(choice => choice.scene === sceneId && choice.pos === pos).
      map(_.text).
      take(1)
  private val updateTextCompiled = Compiled(updateTextQuery _)
  def updateText(sceneId: UUID, pos: Int, text: String): DBIO[Int] =
    updateTextCompiled(sceneId, pos).
      update(text)
}
