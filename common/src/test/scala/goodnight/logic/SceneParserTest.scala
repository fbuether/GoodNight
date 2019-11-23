
package goodnight.logic

import fastparse._
import org.scalatest.FunSpec

import goodnight.model
import goodnight.model.text._



class SceneParserTest extends FunSpec {
  val parsed: (String => SceneParser.ParsedScene) =
    SceneParser.parseScene(_).get

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
  }

  describe("scene texts") {
    it("are anything after the first line") {
      val text = "# head\nbody"
      assert(parsed(text).text == "body")
    }

    describe("may be empty if") {
      it("there is just one line") {
        val text = "head"
        assert(parsed(text).text.isEmpty)
      }

      it("there is just whitespace") {
        val text ="head\n  "
        assert(parsed(text).text.isEmpty)
      }

      it("there is several all-empty lines") {
        val text = "head\n\n  \n \t  \n \t  "
        assert(parsed(text).text.isEmpty)
      }
    }

    it("may span several lines") {
      val text = "head\nbody 1\nbody 2"
      assert(parsed(text).text == "body 1 body 2")
    }

    it("gets whitespace trimmed per line") {
      val text = "head\n  body   \t \n text \n \n \n"
      assert(parsed(text).text == "body text")
    }
  }
}
