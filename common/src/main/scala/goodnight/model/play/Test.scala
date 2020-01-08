
package goodnight.model.play


abstract class Test {
  def quality: Quality[_]
  // def order: Int // the order in which tests should be shown
  def succeeded: Bool // did the current player pass this test?
}
object Test {
  // tests if the player has or does not have the quality.
  case class Boolean(quality: Quality[Sort.Boolean.type],
    succeeded: Bool, // did the player satisfy this test?
    value: Bool)
      extends Test

  // check if the player quality amount satifies `value op other`
  // for example, for IntegerOp(Greater, 7), `quality(player) > 7` must be true.
  case class Integer(quality: Quality[Sort.Integer.type],
    succeeded: Bool, // did the player satisfy this test?
    op: BinaryOperator,
    other: Int)
      extends Test

  def apply(quality: Quality[Sort.Boolean.type], succeeded: Bool, value: Bool) =
    Boolean(quality, succeeded, value)

  def apply(quality: Quality[Sort.Integer.type], succeeded: Bool,
    op: BinaryOperator, other: Int) =
    Integer(quality, succeeded, op, other)
}
