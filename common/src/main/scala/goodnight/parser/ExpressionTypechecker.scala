
package goodnight.parser

import goodnight.model.Expression
import goodnight.model.Expression._
import goodnight.printer.ExpressionPrinter


object ExpressionTypechecker {
  private def p(e: Expression) = "\"" + ExpressionPrinter.print(e) + "\""
  private def p(ty: Type) = ty match {
    case Type.Bool => "boolean"
    case Type.Int => "number" }

  // private def error(e: Expression, ty: Type) =
  //   Left("Error: " + p(e) + " is not a " + p(ty) + ".")

  // private def errorMismatch(e1: Expression, e2: Expression,
  //   ty1: Type, ty2: Type) =
  //   Left("Error: " + p(e1) + " and " + p(e2) + " are of different types " +
  //     "(" + p(ty1) + " and " + p(ty2) + ")")


  private def errorBinary(outer: Expression,
    left: Expression, right: Expression, leftTy: Type, rightTy: Type) =
    Left("Error: " + p(left) + " and " + p(right) + " in " + p(outer) +
      " must be the same type, but are " + p(leftTy) + " and " + p(rightTy) +
      ".")

  private def errorIn(outer: Expression, inner: Expression, reqTy: Type,
    innerTy: Type) =
    Left("Error: " + p(inner) + " in " + p(outer) +
      " should be " + p(reqTy) + ", but is a " + p(innerTy) + ".")


  private def binOp(op: BinaryOperator): (Option[Type], Type) =
    op match {
      case Add | Sub | Mult | Div => (Some(Type.Int), Type.Int)
      case And | Or => (Some(Type.Bool), Type.Bool)
      case Greater | GreaterOrEqual | Less | LessOrEqual =>
        (Some(Type.Int), Type.Bool)
      case Equal | NotEqual => (None, Type.Bool)
    }

  private def expression(c: Context, e: Expression): Either[String, Type] =
    e match {
      case Text(name) =>
        if (c.contains(name)) Right(c(name))
        else Left("Quality \"" + name + "\" does not exist.")
      case Bool(_) => Right(Type.Bool)
      case Number(_) => Right(Type.Int)
      case Unary(op, e1) =>
        val requires = op match { case Not => Type.Bool }
        val e1ty = expression(c, e1)

        if (e1ty == Right(requires)) e1ty.map(_ => Type.Bool)
        else e1ty.flatMap(e1ty => errorIn(e, e1, Type.Bool, e1ty))

      case Binary(op, e1, e2) =>
        val (in, out) = binOp(op)
        val e1ty = expression(c, e1)
        val e2ty = expression(c, e2)

        in match {
          case Some(inTy) =>
            if (e1ty != Right(inTy))
              e1ty.flatMap(e1ty => errorIn(e, e1, inTy, e1ty))
            else if (e2ty != Right(inTy))
              e2ty.flatMap(e2ty => errorIn(e, e2, inTy, e2ty))
            else
              e1ty.flatMap(_ => e2ty.map(_ => out))
          case None =>
            if (e1ty.isRight && e2ty.isRight && e1ty != e2ty)
              errorBinary(e, e1, e2, e1ty.right.get, e2ty.right.get)
            else
              e1ty.flatMap(_ => e2ty.map(_ => out))
        }
    }

  def check(c: Context, e: Expression): Either[String, Type] =
    expression(c, e)
}
