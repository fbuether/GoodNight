
package goodnight.db

import java.util.UUID
import slick.jdbc.PostgresProfile.api._

import goodnight.server.PostgresProfile._
import goodnight.server.PostgresProfile.Table
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


object Choice {
  def apply() = TableQuery[Choice]

  type Q = Query[Choice, model.Choice, Seq]

  def update(sceneId: UUID, pos: Int, newText: String) =
    apply().filter(choice => choice.scene === sceneId && choice.pos === pos).
      take(1).
      map(_.text).
      update(newText)
}
