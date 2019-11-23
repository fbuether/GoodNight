
package goodnight.logic

import fastparse._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._
import goodnight.logic.SceneParser._


class SceneParserTest extends FunSpec with Inside {
  val parsed: (String => SceneParser.ParsedScene) =
    SceneParser.parseScene(_) match {
      case Right(scene) => scene
      case Left(error) => throw new Error(s"Parsing failed with: $error")
    }

  val error: (String => String) =
    SceneParser.parseScene(_) match {
      case Left(error) => error
      case Right(scene) => throw new Error(s"Parsing suceeded with: $scene")
    }

  describe("scene titles") {
    it("are any first line") {
      val text = "first line\nsome text\nwell."
      assert(parsed(text).title == "first line")
    }

    it("are the only line, if nothing else is there") {
      val text = "only a title"
      assert(parsed(text).title == text)
    }

    it("may contain any kind of character") {
      val text = "¬!\"£$%^&*()_+`[]{};\'#:@~,./<?>\\|"
      assert(parsed(text).title == text)
    }

    describe("strip the first uninterrupted group of hashes") {
      it("which works for one hash") {
        val text = "#title"
        assert(parsed(text).title == "title")
      }

      it("works for a hash and a space") {
        val text = "# title"
        assert(parsed(text).title == "title")
      }

      it("works for multiple hashes") {
        val text = "#### title"
        assert(parsed(text).title == "title")
      }

      it("preserves hashes after anything else") {
        val text = "## ## title"
        assert(parsed(text).title == "## title")
      }
    }

    it("trim surrounding whitespace") {
      val text = "#  title\t\t \nbody"
      assert(parsed(text).title == "title")
    }

    it("may contain preceeding line breaks and hashes") {
      val text ="\n\n## title\n\nbody"
      assert(parsed(text).title == "title")
    }

    it("may contain preceeding line breaks") {
      val text ="\n\ntitle\n\nbody"
      assert(parsed(text).title == "title")
    }

    it("may contain whitespace in front of the hash") {
      val text = " # title"
      assert(parsed(text).title == "title")
    }

    describe("may not") {
      it("be empty") {
        val text = ""
        assert(error(text).startsWith("Position 1:1"))
      }

      it("be empty even if a body exists") {
        val text = "##\n\nbody"
        assert(error(text).startsWith("Position 1:3"))
      }
    }
  }

  describe("scene texts") {
    it("are anything after the first line") {
      val text = "# head\nbody"
      assert(parsed(text).text == Markdown(Seq(Paragraph("body"))))
    }

    describe("may be empty if") {
      it("there is just one line") {
        val text = "head"
        assert(parsed(text).text == Markdown(Seq()))
      }

      it("there is just whitespace") {
        val text ="head\n  "
        assert(parsed(text).text == Markdown(Seq()))
      }

      it("there is several all-empty lines") {
        val text = "head\n\n  \n \t  \n \t  "
        assert(parsed(text).text == Markdown(Seq()))
      }
    }

    it("may span several lines") {
      val text = "head\nbody 1\nbody 2"
      assert(parsed(text).text == Markdown(Seq(Paragraph("body 1 body 2"))))
    }

    it("gets whitespace trimmed per line") {
      val text = "head\n  body   \t \n text \n \n \n"
      assert(parsed(text).text == Markdown(Seq(Paragraph("body text"))))
    }

    it("may not contain option text") {
      val text = "#title\nbody\n>f"
      assert(parsed(text).text == Markdown(Seq(Paragraph("body"))))
    }
  }

  describe("scene options") {
    they("may be present") {
      val text = """|#head
                    |body
                    |>option
                    |obody""".stripMargin
      assert(parsed(text) ==
        ParsedScene("head",
          Markdown(Seq(Paragraph("body"))),
          Seq(ParsedOption("option",
            Markdown(Seq(Paragraph("obody")))))))
    }
  }

  describe("whitespace in the text") {
    it("may be inbetween title and text") {
      val text = "#title\n\n\n\n\ntext"
      assert(parsed(text).title == "title")
      assert(parsed(text).text == Markdown(Seq(Paragraph("text"))))
    }
  }
}
