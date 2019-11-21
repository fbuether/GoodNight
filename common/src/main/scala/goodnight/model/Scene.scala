
package goodnight.model

import java.util.UUID


case class Scene(
  id: UUID,
  story: UUID,

  // the textual representation, uninterpreted.
  raw: String,

  // interpreted data, dependent on `raw`.
  title: String,
  urlname: String,
  image: String,
  location: Option[UUID],
  text: String,
  mandatory: Boolean
)
