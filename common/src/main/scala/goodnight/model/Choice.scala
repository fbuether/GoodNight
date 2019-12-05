
package goodnight.model

import java.util.UUID


case class Choice(
  id: UUID,
  scene: UUID,

  // all values (and the existence of a choice) depend on `raw` of `scene`.

  // the index of this choice in relation to the scene itself, to order all
  // choices as intended.
  pos: Int,

  title: String,
  urlname: String,

  text: String
)
