
package goodnight.parser

import fastparse._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import goodnight.model.Expression
import goodnight.model.Expression._

object Single extends Tag("single")

class ExpressionParserTest extends FunSpec {
  val parsed: (String => Expression) =
    ExpressionParser.parse(_) match {
      case Right(expression) => expression
      case Left(error) => throw new Error(error)
    }

  val text = Text("text")
  val n71 = Number(71)
  val n72 = Number(72)
  val n73 = Number(73)

  it("text") {
    assert(parsed("text") == text)
  }

  it("71") {
    assert(parsed("71") == n71)
  }

  describe("unary operators") {
    it("!text") {
      assert(parsed("!text") == Unary(Not, text))
    }

    it("!71") {
      assert(parsed("!71") == Unary(Not, n71))
    }
  }

  describe("unary operators with nested") {
    it("!(!71)") {
      assert(parsed("!(!71)") == Unary(Not, Unary(Not, n71)))
    }
  }

  describe("binary operators") {
    it("71+72") {
      assert(parsed("71+72") == Binary(Add, n71, n72))
    }

    it("71 + 72") {
      assert(parsed("71 + 72") == Binary(Add, n71, n72))
    }

    it("72	-		text") {
      assert(parsed("72	-		text") == Binary(Sub, n72, text))
    }

    it("71*72") {
      assert(parsed("71*72") == Binary(Mult, n71, n72))
    }

    it("71/72") {
      assert(parsed("71/72") == Binary(Div, n71, n72))
    }

    it("text and text") {
      assert(parsed("text and text") == Binary(And, text, text))
    }

    it("71 || text") {
      assert(parsed("71 || text") == Binary(Or, n71, text))
    }

    it("71 > 72") {
      assert(parsed("71 > 72") == Binary(Greater, n71, n72))
    }

    it("71 >= 72") {
      assert(parsed("71 >= 72") == Binary(GreaterOrEqual, n71, n72))
    }

    it("text < text") {
      assert(parsed("text < text") == Binary(Less, text, text))
    }

    it("71 <= 72") {
      assert(parsed("71 <= 72") == Binary(LessOrEqual, n71, n72))
    }

    it("71 = 72") {
      assert(parsed("71 = 72") == Binary(Equal, n71, n72))
    }

    it("71 != 72") {
      assert(parsed("71 != 72") == Binary(NotEqual, n71, n72))
    }
  }

  describe("precedence") {
    it("text * !(text or text)") {
      assert(parsed("text * !(text or text)") ==
        Binary(Mult, text, Unary(Not, Binary(Or, text, text))))
    }

    it("text or text and text = text + text * !(text or text)") {
      assert(parsed(
        "text or text and text = text + text * !(text or text)") ==
        Binary(Or, text,
          Binary(And, text,
            Binary(Equal, text,
              Binary(Add, text,
                Binary(Mult, text,
                  Unary(Not,
                    Binary(Or, text, text))))))))
    }

    it("71 + 72 + 73") {
      assert(parsed("71 + 72 + 73") ==
        Binary(Add, Binary(Add, n71, n72), n73))
    }

    it("text > text <> text") {
      assert(parsed("text = text <> text") ==
        Binary(NotEqual, Binary(Equal, text, text), text))
    }

    it("text > text >= text < text <= text = text <> text") {
      assert(parsed("text > text >= text < text <= text = text <> text") ==
        Binary(NotEqual,
          Binary(Equal,
            Binary(LessOrEqual,
              Binary(Less,
                Binary(GreaterOrEqual,
                  Binary(Greater,
                    text, text),
                  text),
                text),
              text),
            text),
          text))
    }

    it("(text and !text) or (71 > 72 * text)") {
      assert(parsed("(text and !text) or (71 > 72 * text)") ==
        Binary(Or,
          Binary(And, text, Unary(Not, text)),
          Binary(Greater, n71,
            Binary(Mult, n72, text))))
    }
  }
}
