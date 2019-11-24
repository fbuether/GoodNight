
package goodnight.logic

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}


import goodnight.model
import goodnight.model.text._


object SceneParser {
  // single lines, detected by start of the line.
  private def contentLine[_:P]: P[Either[String, String]] =
    P(!">" ~ !"$" ~/ CharsWhile(_ != '\n', 0).!).
      map(line => Left(line.trim))

  private def choiceLine[_:P]: P[String] =
    P(">" ~/ CharsWhile(_ != '\n', 0).!).
      map(line => line.trim)

  private def settingLine[_:P]: P[Either[String, String]] =
    P("$" ~/ CharsWhile(_ != '\n', 0).!).
      map(line => Right(line.trim))

  // a group of content and settings
  private def partContent[_:P]: P[(String, Seq[String])] =
    P((contentLine | settingLine).rep(min = 1, sep = "\n")).
      map({ lines =>
        val (lefts, rights) = lines.partition(_.isLeft)
        (lefts.map(_.left.get).mkString("\n"),
          rights.map(_.right.get).toList)
      })

  // a new choice, as indicated by leading ">" on first line
  private def choice[_:P]: P[PChoice] =
    P(choiceLine ~ ("\n" ~ partContent).?).
      map({ case (firstText, remainder) =>
        val (text, settings) = remainder.getOrElse("", Seq())
        val content = firstText + (if (text.isEmpty) "" else "\n" + text)
        PChoice(content, settings)
      })

  // a list of several choices
  private def choices[_:P]: P[Seq[PChoice]] =
    P(choice.rep(min = 0, sep = "\n"))

  private def sceneOnlyChoices[_:P] = P(choices).
    map(PScene("", Seq(), _))

  private def sceneContentAndMaybeChoices[_:P] =
    P(partContent ~ ("\n" ~ choices).?).
      map(c => PScene(c._1, c._2, c._3.getOrElse(Seq())))

  // a full scene, composed of content and possibly choices or just choices
  private def scene[_:P]: P[PScene] =
    P((sceneContentAndMaybeChoices | sceneOnlyChoices) ~ End)


  case class PChoice(content: String, settings: Seq[String])
  case class PScene(content: String, settings: Seq[String],
    choices: Seq[PChoice])


  def parsePScene(raw: String, detailedErrors: Boolean = false) =
    fastparse.parse(raw, scene(_), verboseFailures = true) match {
      case Parsed.Success(value, _) => Right(value)
      case failure @ Parsed.Failure(_, _, _) =>
        if (detailedErrors) Left(failure.trace().longAggregateMsg)
        else Left(failure.msg)
    }
}

