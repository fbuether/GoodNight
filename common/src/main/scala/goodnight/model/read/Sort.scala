
package goodnight.model.read


sealed trait Sort
object Sort {
  // Boolean: Given to player or not given (e.g. "incapacitated")
  // Scenes can change this value to be given or not, i.e. to true or false.
  // use like:
  // $ set: Incapacitated
  // $ set: Incapacitated = false
  case object Bool extends Sort

  // Integer: An integral value (e.g. "Hitpoints")
  // Scenes can perform basic arithmetic on this
  // $ set: Hitpoints = 10
  // $ set: Hitpoints = Hitpoints - 5
  case object Integer extends Sort
}
