
package goodnight.model


sealed trait Setting
object Setting {
  // Sets the name of the scene
  case class Name(name: String) extends Setting

  // Describes scenes that this scene can continue to
  case class Next(scene: String) extends Setting

  // Denotes this scene as a start for this game
  case object Start extends Setting

  // Sets a quality to a specific value
  case class Set(quality: String, value: Expression) extends Setting

  // A test to be performed when this scene fires
  case class Test(condition: Expression) extends Setting

  // to be executed when a parallel Test succeeds
  case class Success(consequence: Setting) extends Setting

  // to be executed when a parallel Test fails
  case class Failure(consequence: Setting) extends Setting

  // a requirement for this scene
  case class Require(value: Expression) extends Setting

  // always show this scene as an option, even if requirements are missing
  case object ShowAlways extends Setting

  // adds a "return" button to return to a parent scen
  case class Return(scene: String) extends Setting

  // literally includes another scene here, as if it's contents had been written
  // inline. Only difference: removes the Setting Name
  case class Include(scene: String) extends Setting
}


case class Scene(
  story: String, // refers Story.name

  // the textual representation, uninterpreted.
  raw: String,

  // interpreted data, dependent on `raw`.

  // the (extracted) title and its url-representation
  name: String,
  urlname: String,

  // this is all non-setting text
  // todo: possibly containing dynamic elements.
  text: String,

  // all settings of this scene.
  settings: Seq[Setting]
)
