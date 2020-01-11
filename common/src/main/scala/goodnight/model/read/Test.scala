
package goodnight.model.read

import goodnight.model.Expression.BinaryOperator


abstract class Test {
  def quality: Quality[_]
  // def order: Int // the order in which tests should be shown
  def succeeded: Boolean // did the current player pass this test?
}
object Test {
  // tests if the player has or does not have the quality.
  case class Bool(quality: Quality[Sort.Bool.type],
    succeeded: Boolean, // did the player satisfy this test?
    value: Boolean)
      extends Test

  // check if the player quality amount satifies `value op other`
  // for example, for IntegerOp(Greater, 7), `quality(player) > 7` must be true.
  case class Integer(quality: Quality[Sort.Integer.type],
    succeeded: Boolean, // did the player satisfy this test?
    op: BinaryOperator,
    other: Int)
      extends Test

  def apply(quality: Quality[Sort.Bool.type], succeeded: Boolean,
    value: Boolean) =
    Bool(quality, succeeded, value)

  def apply(quality: Quality[Sort.Integer.type], succeeded: Boolean,
    op: BinaryOperator, other: Int) =
    Integer(quality, succeeded, op, other)
}
