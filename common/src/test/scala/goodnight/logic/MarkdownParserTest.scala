
package goodnight.parser

import fastparse._, NoWhitespace._
import org.scalatest.{ FunSpec, EitherValues, Inside }
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._


class MarkdownParserTest extends FunSpec {
  def parsed(raw: String) = MarkdownParser.parse(raw) match {
    case Right(md) => md
    case Left(error) => throw new Error(error) }

  def parseWith[A](f: (P[_]) => P[A], input: String): A =
    fastparse.parse(input, f, verboseFailures = true) match {
      case Parsed.Success(value, _) => value
      case failure @ Parsed.Failure(_,_,_) =>
        throw new Error(failure.trace().longAggregateMsg)
    }

  describe("single methods") {
    import MarkdownParser._

    describe("anyText") {
      it("matches any text") {
        assert(parseWith(anyText(_), "any text") == "any text")
      }

      it("does not match newlines") {
        assert(parseWith(anyText(_), "line one\nline two") == "line one")
      }

      it("parses also * and #") {
        assert(parseWith(anyText(_), "a*#b") == "a*#b")
      }
    }

    describe("text") {
      it("matches any plain text") {
        assert(parseWith(text(_), "any plain text") == "any plain text")
      }

      it("does not parse *") {
        assert(parseWith(text(_), "this*that") == "this")
      }
    }

    describe("number") {
      it("parses any number") {
        assert(parseWith(number(_), "363154") == 363154)
      }

      it("does not consume trailing chars") {
        assert(parseWith(number(_), "51b") == 51)
      }
    }

    describe("emph") {
      it("parses anything in stars") {
        assert(parseWith(emph(_), "*text*") == Emph("text"))
      }

      it("stops at the first star") {
        assert(parseWith(emph(_), "*this*that*those*") == Emph("this"))
      }
    }

    describe("strong") {
      it("parses anything in double stars") {
        assert(parseWith(strong(_), "**text**") ==
          Strong("text"))
      }

      it("stops at the first double star") {
        assert(parseWith(strong(_), "**this**that**those**") ==
          Strong("this"))
      }

      ignore("consumes single stars") {
        assert(parseWith(strong(_), "**this*that**those**") ==
          Strong("this*that"))
      }
    }

    describe("markdownLine") {
      it("consumes markdown as long as possible") {
        assert(parseWith(markdownLine(_),
          "this is *the* text of **the year**!") ==
          Seq(Text("this is "),
            Emph("the"),
            Text(" text of "),
            Strong("the year"),
            Text("!")))
      }
    }

    describe("plainLine") {
      ignore("accepts any plain text and trimms it") {
        assert(parseWith(plainLine(_), " hu! ha! ") ==
          Seq(Text("hu! ha!")))
      }

      it("does not accept empty lines") {
        assertThrows[Error] {
          parseWith(plainLine(_), "\n")
        }
      }
    }
  }

  describe("block elements") {
    import MarkdownParser._

    they("parse headers") {
      assert(parseWith(block(_), "## second level") ==
        Header(2, Seq(Text("second level"))))
    }

    they("parse rulers") {
      assert(parseWith(block(_), "---") ==
        Ruler)
    }

    they("parse lists of a single one-line element") {
      assert(parseWith(block(_), "*element") ==
        List(Seq(Paragraph(Seq(Text("element"))))))
    }

    they("parse lists of two one-line elements") {
      assert(parseWith(block(_), "*element\n*other") ==
        List(Seq(
          Paragraph(Seq(Text("element"))),
          Paragraph(Seq(Text("other"))))))
    }

    they("allow lists to start with spaces before the *") {
      assert(parseWith(block(_), " * first:\n * and then.") ==
        List(Seq(Paragraph(Seq(Text("first:"))),
          Paragraph(Seq(Text("and then."))))))
    }

    they("parse a list with continued item") {
      assert(parseWith(block(_), "* this\n  is\n  multiline") ==
        List(Seq(
          Paragraph(Seq(Text("this"), Text("is"), Text("multiline"))))))
    }

    they("parse enumerations") {
      assert(parseWith(block(_), "1. first\n5. and finally") ==
        Enum(Seq(
          (1, Paragraph(Seq(Text("first")))),
          (5, Paragraph(Seq(Text("and finally")))))))
    }

    they("parse blockquotes") {
      assert(parseWith(block(_), "> quoted") ==
        Blockquote(Seq(Seq(Text("quoted")))))
    }

    they("parse multi-line blockquotes") {
      assert(parseWith(block(_), "> this\n> that") ==
        Blockquote(Seq(Seq(Text("this")), Seq(Text("that")))))
    }
  }

  describe("paragraphs") {
    import MarkdownParser._

    they("parse plain text") {
      assert(parseWith(paragraph(_), "plain text line") ==
        Paragraph(Seq(Text("plain text line"))))
    }

    they("parse multi-line text") {
      assert(parseWith(paragraph(_), "plain\ntext\nlines, yeah.") ==
        Paragraph(Seq(Text("plain"), Text("text"), Text("lines, yeah."))))
    }

    they("do not accept intermediate empty lines") {
      assert(parseWith(paragraph(_), "plain\n\n\n\ntext") ==
        Paragraph(Seq(Text("plain"))))
    }

    they("do not accept only empty lines") {
      assertThrows[Error] {
        parseWith(paragraph(_), "\n\n\n\ntext")
      }
    }
  }

  describe("blocks") {
    import MarkdownParser._
    def test(input: String, result: Seq[Block]): Unit =
      assert(parseWith(blocks(_), input) == result)

    they("can be a single paragraph") {
      test("plain text", Seq(Paragraph(Seq(Text("plain text")))))
    }

    they("can be a two line paragraph") {
      test("plain\ntext", Seq(Paragraph(Seq(Text("plain"), Text("text")))))
    }

    they("can be two paragraphs seperated by two line breaks") {
      test("plain\n\ntext", Seq(Paragraph(Seq(Text("plain"))),
        Paragraph(Seq(Text("text")))))
    }
  }



  describe("markdown paragraphs") {
    they("can be single lines") {
      assert(parsed("single line") ==
        Markdown(Seq(Paragraph(Seq(Text("single line"))))))
    }

    they("can be multiple lines") {
      assert(parsed("one\ntwo\nthree") ==
        Markdown(Seq(Paragraph(Seq(
          Text("one"), Text("two"), Text("three"))))))
    }

    they("are seperated by two line breaks") {
      assert(parsed("one\n\ntwo") ==
        Markdown(Seq(Paragraph(Seq(Text("one"))),
          Paragraph(Seq(Text("two"))))))
    }
  }

  describe("headers") {
    they("start with a #") {
      assert(parsed("# head") ==
        Markdown(Seq(Header(1, Seq(Text("head"))))))
    }

    they("end with a newline") {
      assert(parsed("# head\nbody") ==
        Markdown(Seq(Header(1, Seq(Text("head"))),
          Paragraph(Seq(Text("body"))))))
    }
  }

  describe("markdown") {
    it("ignores leading line breaks") {
      assert(parsed("\n\n\nleaded line") ==
        Markdown(Seq(Paragraph(Seq(Text("leaded line"))))))
    }

    it("ignores trailing line breaks") {
      assert(parsed("\ntrailed line\n\n\n") ==
        Markdown(Seq(Paragraph(Seq(Text("trailed line"))))))
    }
  }

  describe("inline markup") {
    it("allows emphasis at not the start") {
      assert(parsed("heh*emphasised*") ==
        Markdown(Seq(Paragraph(Seq(Text("heh"), Emph("emphasised"))))))
    }

    it("allows strong") {
      assert(parsed("**stronged**") ==
        Markdown(Seq(Paragraph(Seq(Strong("stronged"))))))
    }

    it("mixes with regular text, if on whitespace") {
      assert(parsed("well **awesome**") ==
        Markdown(Seq(Paragraph(Seq(
          Text("well "),
          Strong("awesome"))))))
    }

    it("it preserves leading and trailing whitespace") {
      assert(parsed("well *awesome* yeah") ==
        Markdown(Seq(Paragraph(Seq(
          Text("well "),
          Emph("awesome"),
          Text(" yeah"))))))
    }

    it("parses lines starting with a star as list, not emph") {
      assert(parsed("*foo*bar*") ==
        Markdown(Seq(List(Seq(Paragraph(Seq(
          Text("foo"), Emph("bar"))))))))
    }
  }


  describe("larger examples") {
    they("allow a header followed by a paragraph.") {
      val raw =
        """|# Header.
           |
           | This is certainly interesting, no?""".stripMargin
      val markdown = Markdown(Seq(
        Header(1, Seq(Text("Header."))),
        Paragraph(Seq(Text("This is certainly interesting, no?")))))
      assert(parsed(raw) == markdown)
    }

    they("work with paragraphs, lists and highlight") {
      val raw =
        """|# Welcome.
           |
           |Your *first* day.
           |It is something special.
           |
           |These are your tasks:
           |* Say hello
           |* **Be polite**
           |* Eat something
           |
           |## Further instructions
           |You'll receive those later on.""".stripMargin
      val markdown = Markdown(Seq(
        Header(1, Seq(Text("Welcome."))),
        Paragraph(Seq(
          Text("Your "),
          Emph("first"),
          Text(" day."),
          Text("It is something special."))),
        Paragraph(Seq(
          Text("These are your tasks:"))),
        List(Seq(
          Paragraph(Seq(Text("Say hello"))),
          Paragraph(Seq(Strong("Be polite"))),
          Paragraph(Seq(Text("Eat something"))))),
        Header(2, Seq(Text("Further instructions"))),
        Paragraph(Seq(Text("You'll receive those later on.")))))
      assert(parsed(raw) == markdown)
    }

    they("parse multi-line items correctly") {
      val raw =
        """|# The very first scene
           |
           |Change this scene to be the *start* of you
           |and create scenes as you like. Be aware th
           |need one starting scene! (as given by $sta
           |
           |Otherwise, add these:
           |* A dynamic text with *emphasis*
           |* A blockquote, as shown later on
           |* Items with multiple lines,
           |  like this one.
           |
           |A blockquote looks like this:""".stripMargin
      val markdown = Markdown(Seq(
        Header(1, Seq(Text("The very first scene"))),
        Paragraph(Seq(
          Text("Change this scene to be the "),
          Emph("start"),
          Text(" of you"),
          Text("and create scenes as you like. Be aware th"),
          Text("need one starting scene! (as given by $sta"))),
        Paragraph(Seq(
          Text("Otherwise, add these:"))),
        List(Seq(
          Paragraph(Seq(Text("A dynamic text with "), Emph("emphasis"))),
          Paragraph(Seq(Text("A blockquote, as shown later on"))),
          Paragraph(Seq(Text("Items with multiple lines,"),
            Text("like this one."))))),
        Paragraph(Seq(Text("A blockquote looks like this:")))))
      assert(parsed(raw).elements.take(3) == markdown.elements.take(3))
      val pLs = parsed(raw).elements.collect({ case List(el) => el }).flatten
      val mLs = markdown.elements.collect({ case List(el) => el }).flatten

      pLs.zip(mLs).foreach({ case (p,m) =>
        assert(p == m)
      })

      assert(parsed(raw) == markdown)
    }
  }
}
