
package goodnight.logic

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}


import goodnight.model
import goodnight.model.text._


object SceneParser {

  private def whitespace[_:P] = P(CharsWhileIn(" \n\t", 0))
  private def inlineWhitespace[_:P] = P(CharsWhileIn(" \t", 0))

  private def line[_:P] = P(CharsWhile(_ != '\n').!).
    map(_.trim)
  private def lineEnd[_:P] = P("\n" | End)./



  private def textLine[_:P] = P(!">" ~ line)



  // private def paragraph[_:P]: P[Paragraph] =
  //   P(textLine.rep(min = 1, sep = "\n"./)).
  //     map(lines => Paragraph(lines.mkString(" ")))

  private def paragraph[_:P]: P[Paragraph] =
    P((!">" ~ CharsWhile(_ != '\n', 1).!).rep(min = 1, sep = "\n")).
      map(lines => Paragraph(lines.mkString(" ")))



  // private def blockElements[_:P]: P[Seq[BlockElement]] =
  //   P(paragraph.rep(sep = "\n".rep(min = 2, sep = CharsWhileIn(" \t", 0))))



  private def markdown[_:P]: P[Markdown] = P(
    paragraph
    // whitespace ~/ blockElements ~ whitespace
  ).
    map(p => Markdown(Seq(p)))

  // def markdown = paragraph


  // inline elements




  // instructions
  // private def instructionPrefix[_:P] = P(">" ~/ inlineWhitespace)

  // private def option[_:P] = P(instructionPrefix ~/ line ~ lineEnd ~ markdown).
  //   map(ParsedOption.tupled)


  private def option[_:P]: P[ParsedOption] =
    P(">" ~ CharsWhile(_ != '\n', 1).! ~ "\n" ~ markdown).
      map(ParsedOption.tupled)


  private def options[_:P]: P[Seq[ParsedOption]] =
    P(option.rep(min = 0, sep = "\n"))



  def scene[_:P] =
    P(whitespace ~ CharsWhileIn("#", 0) ~ line ~ lineEnd ~/
      whitespace ~ markdown ~
      whitespace ~ options ~ End).
      map(ParsedScene.tupled)



  case class ParsedOption(
    title: String,
    text: Markdown
  )

  case class ParsedScene(
    title: String,
    text: Markdown,
    options: Seq[ParsedOption]
  )




  def parseMarkdown(raw: String): Either[String, Markdown] =
    fastparse.parse(raw, markdown(_), verboseFailures = true) match {
      case Parsed.Success(md, _) => Right(md)
      case failure @ Parsed.Failure(_, _, _) => Left(failure.msg)
    }


  def parseScene(raw: String): Either[String, ParsedScene] = {
    fastparse.parse(raw, scene(_), verboseFailures = true) match {
      case Parsed.Success(value, _) => Right(value)
      case failure @ Parsed.Failure(_, _, _) =>
        // Left(failure.msg)
        Left(failure.trace().longAggregateMsg)
    }
  }
}

