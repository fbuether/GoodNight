
package goodnight.parser

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}

import goodnight.model


object QualityParser {

  private def content[_:P]: P[Either[String, QSetting]] =
    P(!"$" ~/ BaseParser.remainingLine).
      map(Left.apply)

  // settings

  private def nameSetting[_:P]: P[QSetting] =
    P("name" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.remainingLine).
      map(Name.apply)

  private def booleanSortSetting[_:P]: P[QSetting] =
    P("boolean").
      map(_ => BooleanSort)

  private def enumValueSetting[_:P]: P[QSetting] =
    P("value" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.remainingLine).
      map(EnumSortValue.apply)

  private def intSortSetting[_:P]: P[QSetting] =
    P("range" ~/
      (BaseParser.whitespace ~ ":" ~
        (BaseParser.number.? ~ BaseParser.whitespace ~ "-" ~
          BaseParser.whitespace ~ BaseParser.number.?)).?).
      map({
        case Some((min, max)) => IntSort(min, max)
        case None => IntSort(None, None)
      })

  private def imageSetting[_:P]: P[QSetting] =
    P("image" ~/ BaseParser.whitespace ~ ":" ~ BaseParser.remainingLine).
      map(Image.apply)



  private def anySetting[_:P]: P[QSetting] =
    P((nameSetting | booleanSortSetting | enumValueSetting | intSortSetting |
      imageSetting))

  private def setting[_:P]: P[Either[String, QSetting]] =
    P("$" ~/ BaseParser.whitespace ~ anySetting).
      map(Right.apply)



  private def quality[_:P]: P[PQuality] =
    P(Start ~ (content | setting).rep(min = 1, sep = "\n") ~ End).
      map(lines => PQuality(
        lines.collect({ case Left(t) => t}).mkString("\n"),
        lines.collect({ case Right(t) => t})))


  trait QSetting
  case class Name(name: String) extends QSetting
  case object BooleanSort extends QSetting
  case class EnumSortValue(value: String) extends QSetting
  case class IntSort(min: Option[Int], max: Option[Int]) extends QSetting
  case class Image(image: String) extends QSetting

  case class PQuality(text: String, settings: Seq[QSetting])

  private def nameOfText(text: String): String =
    text.substring(0, text.length.min(20))

  def parse(storyUrlname: String, raw: String): Either[String, model.Quality] =
    fastparse.parse(raw, quality(_), verboseFailures = true) match {
      case failure @ Parsed.Failure(_, _, _) =>
        Left(failure.trace().longAggregateMsg)
      case Parsed.Success(value, _) =>
        val name = value.settings.collect({ case Name(n) => n }).headOption.
          getOrElse(nameOfText(value.text))
        val boolSort = value.settings.
          collect({ case BooleanSort => model.Sort.Boolean }).
          headOption
        val enumValues = model.Sort.Enumeration(
          value.settings.
            collect({ case EnumSortValue(s) => s }))
        val imageSort = value.settings.
          collect({ case IntSort(min,max) => model.Sort.Integer(min,max) }).
          headOption
        val sort =
          if (boolSort.isDefined) boolSort.get
          else if (!enumValues.values.isEmpty) enumValues
          else if (imageSort.isDefined) imageSort.get
          else model.Sort.Boolean
        val image = value.settings.collect({ case Image(i) => i }).headOption.
            getOrElse("X.png")

        Right(model.Quality(storyUrlname, name,
          sort, false, None, image, value.text))
    }
}
