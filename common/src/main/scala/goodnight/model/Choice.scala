
package goodnight.model

import java.util.UUID


case class Choice(
  id: UUID,
  scene: UUID,

  // all values (and the existence of a choice) depend on `raw` of `scene`.

  // the index of this choice in relation to the scene itself, to order all
  // choices as intended.
  order: Int,

  title: String,
  text: String
)


// case class Requirement(
//   id: UUID,
//   choice: UUID,

//   quality: UUID,
//   min: Int,
//   max: Int,
//   // Should *this requirement* be shown if the player meets it?
//   showIfMet: Boolean,
//   // Should *this choice* be shown if the player does not meet this requirement?
//   hidesIfUnmet: Boolean
// )
