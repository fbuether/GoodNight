
package goodnight.db.model

import java.util.UUID


case class Activity (
  id: UUID,
  story: String,
  user: String,
  number: Int,
  scene: String,
  random: List[Int])
    extends DbModel {
  def model// (effects: Seq[Effect])
  = goodnight.model.Activity(
    story, user, number, scene, random,
    Map() // todo.
  )
}
