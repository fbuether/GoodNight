
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

  private def toTest(op: BinaryOperator): String = op match {
    case Add => "+"
    case Sub => "-"
    case Mult => "*"
    case Div => "/"

    case And => "and"
    case Or => "or"

    case Greater => "is more than"
    case GreaterOrEqual => "is at least"
    case Less => "is less than"
    case LessOrEqual => "is at most"
    case Equal => "is"
    case NotEqual => "is not"
  }

  def toTest(e: Expression): String = e match {
    case Text(_) | Number(_) => print(e)
    case Unary(Not, e) => "not " + toTest(e)
    case Binary(op, left, right) =>
      val lr = toTest(left)
      val rr = toTest(right)
      val l = if (lr.contains(" ")) ("(" + lr + ")") else lr
      val r = if (rr.contains(" ")) ("(" + rr + ")") else rr
      l + " " + toTest(op) + " " + r }
}
