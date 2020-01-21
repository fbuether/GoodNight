
package goodnight.stories.read

import goodnight.db
import goodnight.model
import goodnight.printer.ExpressionPrinter


object Expression {
  def evaluate(states: Seq[(db.model.State, db.model.Quality)],
    qualities: Seq[db.model.Quality],
    expr: model.Expression):
      Option[model.Expression.Value] = {
    import goodnight.model.Expression._
    // import goodnight.model.Expression.Value._
    expr match {
      case Text(name) =>
        if (name == "true") Some(Value.Bool(true))
        else if (name == "false") Some(Value.Bool(false))
        else
        // we have to infer the type depending on the referred quality
          states.find(_._2.urlname == name).
            map(state => (state._2.sort, state._1.value)) match {
              case Some((db.model.Sort.Bool, v)) =>
                Some(Value.Bool(v == "true"))
              case Some((db.model.Sort.Integer, v)) =>
                try { Some(Value.Integer(v.toInt)) }
                catch { case _: Exception => None }
              case None => qualities.find(_.urlname == name).map(_.sort) match {
                case Some(db.model.Sort.Bool) => Some(Value.Bool(false))
                case Some(db.model.Sort.Integer) => Some(Value.Integer(0))
                case _ => None } }
      case Bool(b) => Some(Value.Bool(b))
      case Number(n) => Some(Value.Integer(n))
      case Unary(Not, e) => evaluate(states, qualities, e) match {
        case Some(Value.Bool(b)) => Some(Value.Bool(!b))
        case Some(Value.Integer(_)) => None
        case None => None }
      case Binary(op, e1, e2) =>
        val v1 = evaluate(states, qualities, e1)
        val v2 = evaluate(states, qualities, e2)
        (v1, v2) match {
          case (Some(Value.Integer(v1)), Some(Value.Integer(v2))) => op match {
            case Add  => Some(Value.Integer(v1 + v2))
            case Sub  => Some(Value.Integer(v1 - v2))
            case Mult => Some(Value.Integer(v1 * v2))
            case Div  => Some(Value.Integer(v1 / v2))
            case And | Or => None
            case Greater        => Some(Value.Bool(v1 > v2))
            case GreaterOrEqual => Some(Value.Bool(v1 >= v2))
            case Less           => Some(Value.Bool(v1 < v2))
            case LessOrEqual    => Some(Value.Bool(v1 <= v2))
            case Equal          => Some(Value.Bool(v1 == v2))
            case NotEqual       => Some(Value.Bool(v1 != v2)) }
          case (Some(Value.Bool(v1)), Some(Value.Bool(v2))) => op match {
            case Add | Sub | Mult | Div => None
            case And => Some(Value.Bool(v1 && v2))
            case Or  => Some(Value.Bool(v1 || v2))
            case Greater        => Some(Value.Bool(v1 > v2))
            case GreaterOrEqual => Some(Value.Bool(v1 >= v2))
            case Less           => Some(Value.Bool(v1 < v2))
            case LessOrEqual    => Some(Value.Bool(v1 <= v2))
            case Equal          => Some(Value.Bool(v1 == v2))
            case NotEqual       => Some(Value.Bool(v1 != v2)) }
          case _ => None }
    }
  }

  def toString(value: model.Expression.Value): String = value match {
    case model.Expression.Value.Bool(b) => if (b) "true" else "false"
    case model.Expression.Value.Integer(i) => i.toString }

  def toString(value: Option[model.Expression.Value]): String =
    value.map(toString).getOrElse("false")
}
