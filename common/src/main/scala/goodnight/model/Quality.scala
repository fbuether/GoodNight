
package goodnight.model


sealed trait Sort
object Sort {
  // Boolean: Given to player or not given (e.g. "incapacitated")
  // if a player has this quality, it is considered "true"; it's value is
  // "true" if the player possesses this quality, "false" otherwise.
  case object Boolean extends Sort

  // Enumeration: A value from a set of names (e.g. "psychic state")
  // the value of these qualities is the one choosen from the enum.
  case class Enumeration(values: Seq[String]) extends Sort

  // Integer: A integral value, possibly limited (e.g. "hit points")
  // the value of these qualities is the decimal rep. of the integer.
  case class Integer(min: Option[Int], max: Option[Int]) extends Sort
}


case class Quality(
  story: String, // refers Story.urlname

  // the textual representation, uninterpreted.
  raw: String,

  // interpreted data, dependent on `raw`.
  name: String,
  urlname: String,

  sort: Sort,
  image: String,
  description: String
)


// todo: further settings:
// - hidden (does not show in the side bar)
// - scene (a scene to link to when clicked)
