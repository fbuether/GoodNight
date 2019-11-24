
package goodnight.logic

import fastparse._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._
import goodnight.logic.SceneParser._


class SceneParserTest extends FunSpec with Inside {
  val parsed: (String => SceneParser.PScene) =
    SceneParser.parsePScene(_, true) match {
      case Right(scene) => scene
      case Left(error) => throw new Error(s"Parsing failed with: $error")
    }

  describe("scene content") {
    it("can be a single line") {
      assert(parsed("main").content ==
        "main")
    }

    it("can be several lines") {
      assert(parsed("main\nand\nmore").content ==
        "main\nand\nmore")
    }

    it("can be empty") {
      assert(parsed("").content ==
        "")
    }

    it("can have empty lines") {
      assert(parsed("main\n\nmore").content ==
        "main\n\nmore")
    }

    it("can have many empty lines") {
      assert(parsed("main\n\nand\n\n\nmore\n\n\n\n").content ==
        "main\n\nand\n\n\nmore\n\n\n\n")
    }

    it("ignores lines with $") {
      assert(parsed("""|main
                       |$something
                       |
                       |or other""".stripMargin).content ==
        "main\n\nor other")
    }

    it("ignores everything after a line with >") {
      assert(parsed("""|main
                       |>choice
                       |$setting
                       |body""".stripMargin).content ==
        "main")
    }
  }

  describe("scene settings") {
    they("start with $ sign") {
      assert(parsed("$setting").settings ==
        Seq("setting"))
    }

    they("can appear multiple times") {
      assert(parsed("$setting\nbody\n$more").settings ==
        Seq("setting", "more"))
    }

    they("do not contain empty lines") {
      assert(parsed("$setting\n\n$more").settings ==
        Seq("setting", "more"))
    }

    they("can be empty") {
      assert(parsed("$\n$more\n$\nwell.").settings ==
        Seq("", "more", ""))
    }
  }

  describe("scene choices") {
    they("start with the > sign") {
      assert(parsed("body\n>choice") ==
        PScene("body", Seq(),
          Seq(PChoice("choice", Seq()))))
    }

    they("can be the whole body") {
      assert(parsed(">") ==
        PScene("", Seq(), Seq(PChoice("", Seq()))))
    }

    describe("consume all text past the first >") {
      they("as body") {
        assert(parsed(">this\nis\n\ncontent").choices(0).content ==
          "this\nis\n\ncontent")
      }

      they("as settings") {
        assert(parsed(">\n$set\n$set2").choices(0).settings ==
          Seq("set", "set2"))
      }

      they("as settings and body") {
        assert(parsed(">body\n$set\n$set2\nbody2\n\nbody3").choices(0) ==
          PChoice("body\nbody2\n\nbody3", Seq("set", "set2")))
        }
    }

    they("can occur multiple times") {
      assert(parsed(">apple\n>orange") ==
        PScene("", Seq(), Seq(PChoice("apple", Seq()),
          PChoice("orange", Seq()))))
    }

    they("can occur after content and settings") {
      assert(parsed("""|body
                       |$settings
                       |more body
                       |>choice""".stripMargin).choices ==
        Seq(PChoice("choice", Seq())))
    }

    they("consume their own choices, but not earlier ones") {
      assert(parsed("body\n$bodysetting\n>choice\n$setting").
        choices(0).settings ==
        Seq("setting"))
    }
  }
}
