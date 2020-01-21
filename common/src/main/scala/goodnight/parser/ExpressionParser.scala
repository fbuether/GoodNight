
package goodnight.parser

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}

import goodnight.model.Expression
import goodnight.model.Expression._


object ExpressionParser {

  def text[_:P]: P[Expression] =
    P(BaseParser.name).
      map(Text.apply)

  private def number[_:P]: P[Expression] =
    P(CharIn("0-9").rep(1).!).
      map(num => Number(num.toInt))


  private def unaryNot[_:P]: P[Unary] =
    P("!" ~ !"=" ~/ single).
      map(expr => Unary(Not, expr))

  private def unary[_:P]: P[Expression] =
    P(unaryNot)


  private def binaryOperatorAdd[_:P]: P[BinaryOperator] =
    P("+" ~ !"+").map(_ => Add)

  private def binaryOperatorSub[_:P]: P[BinaryOperator] =
    P("-").map(_ => Sub)

  private def binaryOperatorMult[_:P]: P[BinaryOperator] =
    P("*").map(_ => Mult)

  private def binaryOperatorDiv[_:P]: P[BinaryOperator] =
    P("/").map(_ => Div)

  private def binaryOperatorAnd[_:P]: P[BinaryOperator] =
    P("&&" | "and").map(_ => And)

  private def binaryOperatorOr[_:P]: P[BinaryOperator] =
    P("||" | "or").map(_ => Or)

  private def binaryOperatorGreater[_:P]: P[BinaryOperator] =
    P(">").map(_ => Greater)

  private def binaryOperatorGreaterOrEqual[_:P]: P[BinaryOperator] =
    P(">=").map(_ => GreaterOrEqual)

  private def binaryOperatorLess[_:P]: P[BinaryOperator] =
    P("<").map(_ => Less)

  private def binaryOperatorLessOrEqual[_:P]: P[BinaryOperator] =
    P("<=").map(_ => LessOrEqual)

  private def binaryOperatorEqual[_:P]: P[BinaryOperator] =
    P("=").map(_ => Equal)

  private def binaryOperatorNotEqual[_:P]: P[BinaryOperator] =
    P("!=" | "<>").map(_ => NotEqual)


  private def parens[_:P]: P[Expression] =
    P("(" ~/ binaryOr ~ ")")

  def single[_:P]: P[Expression] =
    P(number | text | unary | parens)


  private def ofInfix(e1: Expression, ops: Seq[(BinaryOperator, Expression)]) =
    ops.foldLeft(e1)((e,be) => Binary(be._1, e, be._2))
  private def ofInfixT = Function.tupled(ofInfix _)


  def divMul[_:P]: P[Expression] =
    P(single ~ BaseParser.whitespace ~
      ((binaryOperatorDiv | binaryOperatorMult) ~/
      BaseParser.whitespace ~ single).rep(sep = BaseParser.whitespace)).
      map(ofInfixT)

  def addSub[_:P]: P[Expression] =
    P(divMul ~ BaseParser.whitespace ~
      ((binaryOperatorAdd | binaryOperatorSub) ~/
      BaseParser.whitespace ~ divMul).rep(sep = BaseParser.whitespace)).
      map(ofInfixT)


  def compare[_:P]: P[Expression] =
    P(addSub ~ BaseParser.whitespace ~
      ((binaryOperatorEqual | binaryOperatorNotEqual |
        binaryOperatorGreaterOrEqual | binaryOperatorGreater |
        binaryOperatorLessOrEqual | binaryOperatorLess) ~/
        BaseParser.whitespace ~ addSub).rep(sep = BaseParser.whitespace)).
      map(ofInfixT)

  def binaryAnd[_:P]: P[Expression] =
    P(compare ~ BaseParser.whitespace ~
      (binaryOperatorAnd ~/
        BaseParser.whitespace ~ compare).rep(sep = BaseParser.whitespace)).
      map(ofInfixT)

  def binaryOr[_:P]: P[Expression] =
    P(binaryAnd ~ BaseParser.whitespace ~
      (binaryOperatorOr ~/
        BaseParser.whitespace ~ binaryAnd).
        rep(sep = BaseParser.whitespace)).
      map(ofInfixT)


  def expression[_:P]: P[Expression] =
    P(binaryOr)

  def onlyExpression[_:P] :P[Expression] =
    P(Start ~ binaryOr ~ End)

  def parse(raw: String): Either[String, Expression] =
    fastparse.parse(raw, expression(_), verboseFailures = true) match {
      case Parsed.Success(value, _) => Right(value)
      case failure @ Parsed.Failure(_,_,_) =>
        Left(failure.trace().longAggregateMsg)
    }
}
