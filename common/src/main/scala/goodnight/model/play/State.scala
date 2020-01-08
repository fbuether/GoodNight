
package goodnight.model.play


abstract class State {
  def quality: Quality[_]
  // def order: Int // the order in which these states should be shown
}
object State {
  case class Bool(quality: Quality[Sort.Bool.type], value: Boolean)
      extends State

  case class Integer(quality: Quality[Sort.Integer.type], value: Int)
      extends State

  def apply(quality: Quality[Sort.Bool.type], value: Boolean) =
    Bool(quality, value)

  def apply(quality: Quality[Sort.Integer.type], value: Int) =
    Integer(quality, value)
}
