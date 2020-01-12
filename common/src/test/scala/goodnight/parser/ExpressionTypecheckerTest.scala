
package goodnight.parser

import fastparse._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import goodnight.model.Expression
import goodnight.model.Expression._


class ExpressionTypecheckerTest extends FunSpec {
  def checked(raw: String): Type =
    ExpressionTypechecker.check(context,
      ExpressionParser.parse(raw).right.get) match {
      case Right(t) => t
      case Left(error) => throw new Error(error)
    }

  def errors(raw: String): String =
    ExpressionTypechecker.check(context,
      ExpressionParser.parse(raw).right.get) match {
      case Right(t) => throw new Error(t.toString)
      case Left(msg) => msg
    }

  val boolQ = Text("boolQ")
  val intQ = Text("intQ")
  val n71 = Number(71)
  val n72 = Number(72)
  val n73 = Number(73)
  val context = Map("boolQ" -> Type.Bool, "intQ" -> Type.Int)

  it("boolQ") {
    assert(checked("boolQ") == Type.Bool)
  }

  it("71") {
    assert(checked("71") == Type.Int)
  }

  it("unknown") {
    assert(errors("unknown") == "Quality \"unknown\" does not exist.")
  }

  describe("unary operators") {
    it("!boolQ") {
      assert(checked("!boolQ") == Type.Bool)
    }

    it("!71") {
      assert(errors("!71") ==
        "Error: \"71\" in \"!(71)\" should be boolean, but is a number.")
    }

    it("!intQ") {
      assert(errors("!intQ") ==
        "Error: \"intQ\" in \"!(intQ)\" should be boolean, but is a number.")
    }
  }

  describe("unary operators with nested") {
    it("!(!boolQ)") {
      assert(checked("!(!boolQ)") == Type.Bool)
    }
  }

  describe("binary operators") {
    it("71+72") {
      assert(checked("71+72") == Type.Int)
    }

    it("71 + 72") {
      assert(checked("71 + 72") == Type.Int)
    }

    it("72	-		boolQ") {
      assert(errors("72	-		boolQ") ==
        "Error: \"boolQ\" in \"72-boolQ\" should be number, but is a boolean.")
    }

    it("71*72") {
      assert(checked("71*72") == Type.Int)
    }

    it("71/72") {
      assert(checked("71/72") == Type.Int)
    }

    it("boolQ and boolQ") {
      assert(checked("boolQ and boolQ") == Type.Bool)
    }

    it("71 || boolQ") {
      assert(errors("71 || boolQ") ==
        "Error: \"71\" in \"71 or boolQ\" should be boolean, but is a number.")
    }

    it("71 > 72") {
      assert(checked("71 > 72") == Type.Bool)
    }

    it("71 >= 72") {
      assert(checked("71 >= 72") == Type.Bool)
    }

    it("intQ < boolQ") {
      assert(errors("intQ < boolQ") ==
        "Error: \"boolQ\" in \"intQ<boolQ\" should be number, but is a boolean.")
    }

    it("71 <= 72") {
      assert(checked("71 <= 72") == Type.Bool)
    }

    it("71 = 72") {
      assert(checked("71 = 72") == Type.Bool)
    }

    it("boolQ != boolQ") {
      assert(checked("boolQ != boolQ") == Type.Bool)
    }

    it("boolQ != intQ") {
      assert(errors("boolQ != intQ") ==
        "Error: \"boolQ\" and \"intQ\" in \"boolQ!=intQ\" must be the same type, but are boolean and number.")
    }
  }

  describe("complex expressions") {
    it("boolQ <> !(boolQ or boolQ)") {
      assert(checked("boolQ <> !(boolQ or boolQ)") == Type.Bool)
    }

    it("boolQ or boolQ and boolQ = boolQ + boolQ * !(boolQ or boolQ)") {
      assert(errors(
        "boolQ or boolQ and boolQ = boolQ + boolQ * !(boolQ or boolQ)") ==
        "Error: \"boolQ\" in \"boolQ+boolQ*!(boolQ or boolQ)\" should be number, but is a boolean.")
    }

    it("71 + 72 + 73") {
      assert(checked("71 + 72 + 73") == Type.Int)
    }

    it("boolQ > boolQ <> boolQ") {
      assert(checked("boolQ = boolQ <> boolQ") == Type.Bool)
    }

    it("boolQ > boolQ >= boolQ < boolQ <= boolQ = boolQ <> boolQ") {
      assert(errors("boolQ > boolQ >= boolQ < boolQ <= boolQ = boolQ <> boolQ") ==
        "Error: \"boolQ\" in \"boolQ>boolQ\" should be number, but is a boolean.")
    }

    it("(boolQ and !boolQ) or (71 > 72 * intQ)") {
      assert(checked("(boolQ and !boolQ) or (71 > 72 * intQ)") ==
        Type.Bool)
    }
  }
}
