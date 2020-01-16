
package goodnight.model


// Expressions are computations for values
sealed trait Expression

object Expression {
  // for now, Text always refers to a quality urlname. Names are not allowed,
  // for complexity of lookup.
  case class Text(value: String) extends Expression

  case class Number(value: Int) extends Expression

  // generates a random number in [min,max], including both limits.
  // useful to compare with qualities, or to have extrordinary results
  // case class Random(
  //   min: Int, max: Int)
  //     extends Expression


  sealed trait UnaryOperator
  case object Not extends UnaryOperator // Boolean

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

  sealed trait Type
  object Type {
    case object Int extends Type
    case object Bool extends Type
  }

  type Context = Map[String, Type]
}
