
package goodnight.logic

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}

import goodnight.model
import goodnight.model.text._


object SceneParser {
  // todo: settings embedded into lines
  private def content[_:P]: P[Either[String, model.Setting]] =
    P(!"$" ~/ CharsWhile(_ != '\n', 0).!).
      map(text => Left(text.replaceAll("\\s+$", "")))

  // settings

  private def nameSetting[_:P]: P[model.Setting] =
    P("name" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.remainingLine).
      map(model.Setting.Name.apply)

  private def nextSetting[_:P]: P[model.Setting] =
    P("next" ~/ BaseParser.whitespace ~ ":" ~ BaseParser.remainingLine).
      map(model.Setting.Next.apply)

  private def startSetting[_:P]: P[model.Setting] =
    P("start" ~/ BaseParser.whitespace).
      map(_ => model.Setting.Start)

  private def setSetting[_:P]: P[model.Setting] =
    P("set" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.whitespace ~ BaseParser.name ~ BaseParser.whitespace ~ "=" ~
      BaseParser.whitespace ~ ExpressionParser.expression).
      map(model.Setting.Set.tupled)

  private def testSetting[_:P]: P[model.Setting] =
    P("test" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.whitespace ~ ExpressionParser.expression).
      map(model.Setting.Test.apply)

  private def successSetting[_:P]: P[model.Setting] =
    P("success" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.whitespace ~ anySetting).
      map(model.Setting.Success.apply)

  private def failureSetting[_:P]: P[model.Setting] =
    P("failure" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.whitespace ~ anySetting).
      map(model.Setting.Failure.apply)

  private def requireSetting[_:P]: P[model.Setting] =
    P("require" ~/ BaseParser.whitespace ~ ":" ~
      BaseParser.whitespace ~ ExpressionParser.expression).
      map(model.Setting.Require.apply)

  private def showAlwaysSetting[_:P]: P[model.Setting] =
    P(("show" ~/ BaseParser.whitespace ~ "always" ~ BaseParser.whitespace) |
      ("always" ~/ BaseParser.whitespace ~ "show" ~ BaseParser.whitespace)).
      map(_ => model.Setting.ShowAlways)

  private def returnSetting[_:P]: P[model.Setting] =
    P("return" ~/ BaseParser.whitespace ~ ":" ~ BaseParser.remainingLine).
      map(model.Setting.Return.apply)

  private def includeSetting[_:P]: P[model.Setting] =
    P("include" ~/ BaseParser.whitespace ~ ":" ~ BaseParser.remainingLine).
      map(model.Setting.Include.apply)



  private def anySetting[_:P]: P[model.Setting] =
    P((nameSetting | nextSetting | startSetting | setSetting |
      testSetting | successSetting | failureSetting | requireSetting |
      showAlwaysSetting | returnSetting | includeSetting))

  private def setting[_:P]: P[Either[String, model.Setting]] =
    P("$" ~/ BaseParser.whitespace ~ anySetting).
      map(Right.apply)


  // full scenes
  private def scene[_:P]: P[PScene] =
    P(Start ~ (content | setting).rep(min = 1, sep = "\n") ~ End).
      map(lines => PScene(
        lines.collect({ case Left(t) => t}).mkString("\n"),
        lines.collect({ case Right(t) => t})))


  // case class PChoice(content: String, settings: Seq[String])
  case class PScene(content: String, settings: Seq[model.Setting])


  def parsePScene(raw: String, detailedErrors: Boolean = false) =
    fastparse.parse(raw, scene(_), verboseFailures = true) match {
      case Parsed.Success(value, _) => Right(value)
      case failure @ Parsed.Failure(_, _, _) =>
        if (detailedErrors) Left(failure.trace().longAggregateMsg)
        else Left(failure.msg)
    }


  private def titleOfContent(content: String): String =
    content.substring(0, content.length.min(20))

  def parseScene(story: model.Story, raw: String):
      Either[String, model.Scene] =
    parsePScene(raw, false).map({ pScene =>
      val title = pScene.settings.
        collect({ case model.Setting.Name(n) => n }).
        headOption.
        getOrElse(titleOfContent(pScene.content))
      model.Scene(story.urlname,
        raw,
        title,
        goodnight.urlnameOf(title),
        pScene.content,
        pScene.settings)
    })
}

