
package goodnight.model.play

import java.lang.{Boolean => Bool}


abstract class State {
  def quality: Quality[_]
  // def order: Int // the order in which these states should be shown
}
object State {
  case class Boolean(quality: Quality[Sort.Boolean.type], value: Bool)
      extends State

  case class Integer(quality: Quality[Sort.Integer.type], value: Int)
      extends State

  def apply(quality: Quality[Sort.Boolean.type], value: Bool) =
    Boolean(quality, value)

  def apply(quality: Quality[Sort.Integer.type], value: Int) =
    Integer(quality, value)
}
