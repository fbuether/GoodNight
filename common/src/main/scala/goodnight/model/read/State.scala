
package goodnight.model.read


sealed trait State {
  def quality: Quality
  // def order: Int // the order in which these states should be shown
}
object State {
  @upickle.implicits.key("State.Bool")
  case class Bool(quality: Quality.Bool, value: Boolean)
      extends State

  @upickle.implicits.key("State.Integer")
  case class Integer(quality: Quality.Integer, value: Int)
      extends State

  def apply(quality: Quality.Bool, value: Boolean) =
    Bool(quality, value)

  def apply(quality: Quality.Integer, value: Int) =
    Integer(quality, value)
}
