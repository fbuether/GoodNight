
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
  def title = column[String]("title")
  def urlname = column[String]("urlname")
  def text = column[String]("text")

  def * = ((id, scene, pos, title, urlname, text) <>
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


  private def ofSceneQuery(sceneId: Rep[UUID]) =
    apply().
      filter(_.scene === sceneId).
      sortBy(_.pos)
  private def ofSceneCompiled = Compiled(ofSceneQuery _)
  def ofScene(sceneId: UUID): DBIO[Seq[model.Choice]] =
    ofSceneCompiled(sceneId).result


  private def ofUrlnameQuery(story: Rep[String], scene: Rep[String],
    choice: Rep[String]) =
    apply().
      filter(_.urlname === choice).
      join(Scene().filter(_.urlname === scene)).on(_.scene === _.id).
      join(Story().filter(_.urlname === story)).on(_._2.story === _.id).
      map(_._1._1).
      take(1)
  private def ofUrlnameCompiled = Compiled(ofUrlnameQuery _)
  def ofUrlname(story: String, scene: String, choice: String):
      DBIO[Option[model.Choice]] =
    ofUrlnameCompiled(story, scene, choice).result.headOption


  private def byIdQuery(id: Rep[UUID]) =
    apply().
      filter(_.id === id)
  private def byIdCompiled = Compiled(byIdQuery _)


  def update(id: UUID, newChoice: model.Choice): DBIO[Int] =
    byIdCompiled(id).
      update(newChoice.copy(id = id))
}
