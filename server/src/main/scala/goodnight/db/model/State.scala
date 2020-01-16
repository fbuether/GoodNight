
package goodnight.db.model

import java.util.UUID


case class State(
  id: UUID,
  user: String, // references player.user
  story: String, // references player.story
  quality: String, // refers quality.urlname
  value: String)
    extends DbModel
