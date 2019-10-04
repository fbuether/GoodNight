
package goodnight.model

import java.util.UUID


case class Story(
  id: UUID,
  creator: UUID,
  name: String,
  urlname: String,
  image: String,
  description: String,
  // theme: String,
  startLocation: Option[UUID]
)
