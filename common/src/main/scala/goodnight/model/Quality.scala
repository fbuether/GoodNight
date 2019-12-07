
package goodnight.model


// currently unused.

case class Quality(
  story: String,

  // the textual representation, uninterpreted.
  raw: String,

  // interpreted data, dependent on `raw`.
  name: String,
  description: String,
  // min: Option[Int],
  // max: Option[Int]
)


// // Qualities may have names at certain values, or at each value.
// case class QualityName(
//   id: UUID,
//   quality: UUID,

//   value: Int,
//   name: String
// )
