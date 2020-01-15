
package goodnight.parser

import fastparse._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._
import goodnight.parser.SceneParser._


class QualityParserTest extends FunSpec with Inside {
  val parsed: (String => model.Quality) =
    QualityParser.parse("story", _) match {
      case Right(scene) => scene
      case Left(error) => throw new Error(error)
    }

  describe("quality content") {
    ignore("can have no settings at all") {
      assert(parsed("this is a simple quality") ==
        model.Quality("story",
          "this is a simple qua",
          model.Sort.Boolean,
          false,
          None,
          // "this is a simple qua",
          // "this-is-a-simple-qua",
          // model.Sort.Boolean,
          "X.png",
          "this is a simple quality"))
    }
  }
}
