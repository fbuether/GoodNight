
package goodnight.model.play



case class Quality[A <: Sort](
  story: String, // refers Story.urlname
  urlname: String, // refers edit.Quality.urlname

  sort: A,
  name: String, // the Quality's display name
  // hidden: Boolean,
  // scene: Option[String], // refers Scene.urlname
  image: String)
  // description: String)


sealed trait Sort
object Sort {
  // Boolean: Given to player or not given (e.g. "incapacitated")
  // Scenes can change this value to be given or not, i.e. to true or false.
  // use like:
  // $ set: Incapacitated
  // $ set: Incapacitated = false
  case object Boolean extends Sort

  // Integer: An integral value (e.g. "Hitpoints")
  // Scenes can perform basic arithmetic on this
  // $ set: Hitpoints = 10
  // $ set: Hitpoints = Hitpoints - 5
  case object Integer extends Sort
}
