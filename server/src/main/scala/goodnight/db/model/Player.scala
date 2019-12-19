
package goodnight.db.model

import java.util.UUID


case class Player(
  id: UUID,
  user: String, // refers User.name
  story: String, // refers Story.urlname
  name: String)
    extends DbModel {
  def model(state: Seq[State]) =
    goodnight.model.Player(user, story, name,
      state.map(s => (s.quality, s.value)).toMap)
}
