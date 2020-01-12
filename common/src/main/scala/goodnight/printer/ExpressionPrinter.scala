
package goodnight.printer

import goodnight.model.Expression
import goodnight.model.Expression._


object ExpressionPrinter {

  private def print(op: BinaryOperator) = op match {
    case Add => "+"
    case Sub => "-"
    case Mult => "*"
    case Div => "/"

    case And => " and "
    case Or => " or "

    case Greater => ">"
    case GreaterOrEqual => ">="
    case Less => "<"
    case LessOrEqual => "<="
    case Equal => "="
    case NotEqual => "!="
  }

  def print(e: Expression): String = e match {
    case Text(name) =>
      if (name.contains(" ")) "\"" + name + "\""
      else name
    case Number(num) => num.toString
    case Unary(Not, e) => "!(" + print(e) + ")"
    case Binary(op, left, right) =>
      print(left) + print(op) + print(right)
  }
}
