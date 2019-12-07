
package goodnight.model


// Expressions are computations for values
sealed trait Expression

object Expression {
  case class Quality(
    quality: String) // refers Quality.name
      extends Expression


  case class Literal(
    value: String) // this may be a number in string representation
      extends Expression


  // generates a random number in [min,max], including both limits.
  // useful to compare with qualities, or to have extrordinary results
  case class Random(
    min: Int, max: Int)
      extends Expression


  sealed trait UnaryOperator
  case object Not extends UnaryOperator // Boolean
  case object PlusOne extends UnaryOperator // Integer

  case class Unary(
    operator: UnaryOperator,
    expr: Expression)
      extends Expression


  sealed trait BinaryOperator
  case object Add extends BinaryOperator // Integer
  case object Sub extends BinaryOperator // Integer
  case object Mult extends BinaryOperator // Integer
  case object Div extends BinaryOperator // Integer

  case object And extends BinaryOperator // Boolean
  case object Or extends BinaryOperator // Boolean

  case object Greater extends BinaryOperator // Int
  case object GreaterOrEqual extends BinaryOperator // Int
  case object Less extends BinaryOperator // Int
  case object LessOrEqual extends BinaryOperator // Int
  case object Equal extends BinaryOperator // T
  case object NotEqual extends BinaryOperator // T

  case class Binary(
    operator: BinaryOperator,
    left: Expression,
    right: Expression)
      extends Expression
}
