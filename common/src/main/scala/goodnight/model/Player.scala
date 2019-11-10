
package goodnight.model

import java.util.UUID


case class Player(
  id: UUID,
  user: UUID,
  story: UUID,
  name: String,
  location: Option[UUID]
)
