
package goodnight.model.read

import goodnight.model.Expression.BinaryOperator


sealed trait Test {
  def quality: Quality
  // def order: Int // the order in which tests should be shown
  def succeeded: Boolean // did the current player pass this test?
}
object Test {
  // tests if the player has or does not have the quality.
  @upickle.implicits.key("Test.Bool")
  case class Bool(quality: Quality.Bool,
    succeeded: Boolean, // did the player satisfy this test?
    value: Boolean)
      extends Test

  // check if the player quality amount satifies `value op other`
  // for example, for IntegerOp(Greater, 7), `quality(player) > 7` must be true.
  @upickle.implicits.key("Test.Integer")
  case class Integer(quality: Quality.Integer,
    succeeded: Boolean, // did the player satisfy this test?
    op: BinaryOperator,
    other: Int)
      extends Test

  def apply(quality: Quality.Bool, succeeded: Boolean,
    value: Boolean) =
    Bool(quality, succeeded, value)

  def apply(quality: Quality.Integer, succeeded: Boolean,
    op: BinaryOperator, other: Int) =
    Integer(quality, succeeded, op, other)
}
