
package goodnight.logic

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}


import goodnight.model
import goodnight.model.text._


object SceneParser {

  private def whitespace[_:P] = P(CharsWhileIn(" \n\t"))


  private def line[_:P] = P(CharsWhile(_ != '\n', 1).!).
    map(_.trim)
  private def lineEnd[_:P] = P("\n" | End)

  private def paragraph[_:P] = P((line ~ lineEnd).rep ~ lineEnd).
    map(_.filter(_ != "").mkString(" "))


  // headings
  private def headingPrefix[_:P] = P("#".rep(1).!).
    map(_.length)

  private def heading[_:P] = P(headingPrefix ~ line ~ lineEnd)


  // inline elements



  // parse scenes.


  case class ParsedScene(
    title: String,
    text: String,
    options: Seq[String]
  )



  def scene[_:P] =
    P(whitespace.? ~/
      headingPrefix.? ~/
      line ~ lineEnd ~/
      paragraph.rep(min=1,max=20) ~ End).
      map({ case (title, body) =>
        ParsedScene(title, body.headOption.getOrElse("<nothin'>"), Seq()) })




  def parseScene(raw: String): Try[ParsedScene] = {
    fastparse.parse(raw, scene(_)) match {
      case Parsed.Failure(_, _, extra) =>
        Failure(new Error("Parsing: \"" + raw + "\"; Error: " + extra.trace(true).longAggregateMsg))
      case Parsed.Success(value, _) =>
        Success(value)
    }
  }
}

