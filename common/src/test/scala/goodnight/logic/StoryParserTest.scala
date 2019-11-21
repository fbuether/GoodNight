
package goodnight.logic

import fastparse._
import org.scalatest.FunSpec

import goodnight.model.text._



class StoryParserTest extends FunSpec {


  describe("a story input") {
    val story = """
# titel

dies ist der inhalt.

und ein zwei-zeiliger
Absatz.

* Option 1

* Option 2
""";
  }

  describe("test parsing") {
    it("works on single lines") {
      val text = "ich bin ein einfacher text mit Punkt."
      val result = Sequence(Seq(Plain(text)))

      assert(StoryParser.parse(text).get.value == result)
    }

    it("works on lines with hashes") {
      val text = "ich bin ein Text mit # Hash."
      val result = Sequence(Seq(Plain(text)))

      assert(StoryParser.parse(text).get.value == result)
    }

    it("works on lines starting with hashes") {
      val text = "was fuer ein Text."
      val result = Sequence(Seq(Heading(Plain(text))))

      assert(StoryParser.parse("#" + text).get.value == result)
    }

    it("works on multi-line texts") {
      val text = """|was fuer ein Text.
                    |auch hier noch.""".stripMargin
      val result = Sequence(Seq(Plain("was fuer ein Text."),
        Plain("auch hier noch.")))

      assert(StoryParser.parse(text).get.value == result)
    }

    it ("does not consume trailing or leading whitespace") {
      val text = "  hui.  "
      val result = Sequence(Seq(Plain(text)))

      assert(StoryParser.parse(text).get.value == result)
    }

    it ("it accepts only whitespace.") {
      val text = "     "
      val result = Sequence(Seq(Plain(text)))

      assert(StoryParser.parse(text).get.value == result)
    }
  }
}
