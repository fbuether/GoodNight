
package goodnight.logic

import fastparse._
import org.scalatest.{ FunSpec, EitherValues, Inside }
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._


class MarkdownParserTest {


// class MarkdownTest extends FunSpec with EitherValues with Inside {
//   def parsed(raw: String) = SceneParser.parseMarkdown(raw) match {
//     case Right(md) => md
//     case Left(error) => throw new Error(s"Parsing failed with: $error") }

//   def error(raw: String) = SceneParser.parseMarkdown(raw) match {
//     case Left(error) => error
//     case Right(md) => throw new Error(s"Parsing suceeded with: $md") }

//   describe("a markdown paragraph") {
//     it("parses as a single paragraph") {
//       val text = "I am a\nparagraph\nwith four\nlines"
//       val markdown = SceneParser.parseMarkdown(text)

//       inside (markdown) { case Right(Markdown(elements)) =>
//         assert(elements.length == 1)
//         inside(elements.head) { case Paragraph(text) =>
//           assert(text == "I am a paragraph with four lines")
//         }
//       }
//     }
//   }


//   describe("multiple paragraphs") {
//     describe("can be seperated by multiple newlines") {
//       it("works with 2 newlines") {
//         val text = "par\n\npar"
//         cancel
//       }

//       it("works with 3 newlines") {
//         val text = "par\n\n\npar"
//         cancel
//       }
//     }
//   }
// }

}
