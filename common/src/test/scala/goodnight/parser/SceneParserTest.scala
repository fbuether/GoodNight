
package goodnight.parser

import fastparse._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._
import goodnight.parser.SceneParser._

object Single extends Tag("single")

class SceneParserTest extends FunSpec with Inside {
  val parsed: (String => SceneParser.PScene) =
    SceneParser.parsePScene(_, false) match {
      case Right(scene) => scene
      case Left(error) => throw new Error(error)
    }

  val parserStory = model.Story("--", "storyname", "storyurlname",
    "storyimage", "storydescription", true)

  val fullParsed: (String => model.Scene) =
    SceneParser.parseScene(parserStory, _) match {
      case Right(scene) => scene
      case Left(error) => throw new Error(error)
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
                       |$start
                       |
                       |or other""".stripMargin).content ==
        "main\n\nor other")
    }
  }

  describe("scene settings") {
    they("start with $ sign") {
      assert(parsed("$start").settings ==
        Seq(model.Setting.Start))
    }

    they("can appear multiple times") {
      assert(parsed("$name: name\nbody\n$start").settings ==
        Seq(model.Setting.Name("name"), model.Setting.Start))
    }

    they("do not contain empty lines") {
      assert(parsed("$name:name\n\n$start").settings ==
        Seq(model.Setting.Name("name"), model.Setting.Start))
    }

    they("may have whitespace in front") {
      assert(parsed("$    	  	start").settings ==
        Seq(model.Setting.Start))
    }

    they("can not be empty") {
      assert(intercept[Error]({ parsed("$\n$more\n$\nwell.") }).getMessage().
        startsWith("Expected (nameSetting"))
    }

    they("cannot be random text") {
      assert(intercept[Error]({ parsed("$foobar") }).getMessage().
        startsWith("Expected (nameSetting"))
    }

    describe("specific ones") {
      it("name with value") {
        assert(parsed("$ name: beginning").settings(0) ==
          model.Setting.Name("beginning"))
      }

      it("next with scene name") {
        assert(parsed("$ next: after dinner").settings(0) ==
          model.Setting.Next("after dinner"))
      }

      it("start") {
        assert(parsed("$ start").settings(0) ==
          model.Setting.Start)
      }

      it("set with quality name and value") {
        assert(parsed("$ set: quality = 17").settings(0) ==
          model.Setting.Set("quality",
            model.Expression.Number(17)))
      }

      it("test with condition") {
        assert(parsed("$ test: \"in good light\"").settings(0) ==
          model.Setting.Test(model.Expression.Text("in good light")))
      }

      it("success with new setting") {
        assert(parsed("$ success: next: home").settings(0) ==
          model.Setting.Success(model.Setting.Next("home")))
      }

      it("failure with new setting") {
        assert(parsed("$ failure: set: doomed = true").settings(0) ==
          model.Setting.Failure(
            model.Setting.Set("doomed",
              model.Expression.Text("true"))))
      }

      it("require with condition") {
        assert(parsed("$ require: doomed = true").settings(0) ==
          model.Setting.Require(
            model.Expression.Binary(
              model.Expression.Equal,
              model.Expression.Text("doomed"),
              model.Expression.Text("true"))))
      }

      it("require with \"feige > 2\"") {
        assert(parsed("$require: feige > 2").settings(0) ==
          model.Setting.Require(
            model.Expression.Binary(
              model.Expression.Greater,
              model.Expression.Text("feige"),
              model.Expression.Number(2))))
      }

      it("showAlways") {
        assert(parsed("$ show always").settings(0) ==
          model.Setting.ShowAlways)
      }

      it("showAlways backwards") {
        assert(parsed("$ always show").settings(0) ==
          model.Setting.ShowAlways)
      }

      it("return with scene") {
        assert(parsed("$ return: home").settings(0) ==
          model.Setting.Return("home"))
      }

      it("include with scene") {
        assert(parsed("$ include: cupboard").settings(0) ==
          model.Setting.Include("cupboard"))
      }
    }

    describe("complex tests") {
      it("for comparsion to numeric value") {
        assert(parsed("$ test: power > 5").settings(0) ==
          model.Setting.Test(
            model.Expression.Binary(
              model.Expression.Greater,
              model.Expression.Text("power"),
              model.Expression.Number(5))))
      }
    }
  }


  describe("real scenes") {
    it("from der-letzte-beutezug/und-es-beginnt") {
      assert(fullParsed("""|$ start
                       |$ name: und-es-beginnt
                       |
                       |# Und es beginnt...
                       |
                       |Endlich! Die lange Zeit der Planung zahlt sich aus: Vor dir, das Anwesen des (noch!) reichsten Mannes der Stadt, des Barons von Opulantz; über Dir der tief wolkenbehangene Nachthimmel; und in deiner Tasche: Die Zahlenkombination zum Tresor von Opulantz.
                       |
                       |Wenn alles klappt, wird dies der letzte Beutezug deines Lebens. Du hast dir deine Zukunft schon umfangreich ausgemalt: Ein kleines Haus, irgendwo auf dem Land, in dem du mit deiner großen Liebe gemütlich deinen Lebensabend verbringen wirst, bei Wein und Kaviar an einem warmen Kaminofen...
                       |
                       |Aber erstmal musst du den Baron um seinen monetären Ballast erleichtern. Du weißt, dass es diesmal klappt, denn immerhin...
                       |
                       |$ next: start-dieb
                       |$ next: start-geraet
                       |$ next: start-planer""".stripMargin).settings.
        length == 5)
    }

    it("from das-labyrinth/first-scene") {
      assert(fullParsed("""|$start
                           |$name: First Scene
                           |
                           |# Am Eingang
                           |
                           |Und auf einmal stehst du drin, in dem Labyrinth. Nach der überraschend kurzen Orientierung, in der dir praktisch nichts mitgeteilt wurde, hat man dich durch das Eingangstor geschoben, hinter dir verriegelt, und dich mit dir selbst allein gelassen. Zugegebenermaßen, du hast es dir ja auch freiwillig ausgesucht.
                           |
                           |Vor dir sind befinden sich, nebeneinander, zwei steinerne Treppen: Eine führt nach oben, die andere nach unten. Beide machen bald eine Kurve, Du kannst also nicht erkennen, wo sie hin führen.
                           |
                           |$require: feige > 2
                           |
                           |
                           |$next: treppe-nach-oben
                           |$next: treppe-nach-unten
                           |$next: abwarten""".stripMargin).settings ==
        Seq(model.Setting.Start,
          model.Setting.Name("First Scene"),
          model.Setting.Require(
            model.Expression.Binary(
              model.Expression.Greater,
              model.Expression.Text("feige"),
              model.Expression.Number(2))),
          model.Setting.Next("treppe-nach-oben"),
          model.Setting.Next("treppe-nach-unten"),
          model.Setting.Next("abwarten")))
    }
  }
}
