
package goodnight.model.play

import java.lang.{Boolean => Bool}
import goodnight.model.Expression.BinaryOperator


case class Scene(
  story: String, // refers Story.urlname
  urlname: String, // refers edit.Scene.urlname
  text: String, // the text shown as this story, markdown
  // returnsTo: Option[String], // if this scene has a return, its urlname
  choices: Seq[Choice]) // all options available to the current player


case class Choice(
  urlname: String, // refers Scene.urlname
  text: String, // the text shown as this choice, markdown
  available: Boolean, // can this choice be taken?
  tests: Seq[Test]) // the tests required to take this choice


abstract class Test {
  def quality: Quality[_]
  def succeeded: Bool
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
