
package goodnight.db.model

import java.util.UUID


case class Activity (
  id: UUID,
  story: String, // refers story.urlname
  user: String, // refers player.user
  number: Int,
  scene: String, // refers scene.urlname
  random: List[Int])
    extends DbModel
