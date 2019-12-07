
package goodnight.logic

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}


import goodnight.model
import goodnight.model.text._


object SceneParser {
  private def whitespace[_:P]: P[Unit] =
    P((" " | "\t").rep(0))

  private def remainingLine[_:P]: P[String] =
    P(CharsWhile(_ != '\n', 0).!).
      map(_.trim)

  private def name[_:P]: P[String] =
    P(CharsWhileIn("[a-zA-Z0-9 ]").!).
      map(_.trim)


  // content

  // todo: settings embedded into lines
  private def content[_:P]: P[Either[String, model.Setting]] =
    P(!"$" ~/ remainingLine).
      map(Left.apply)


  // expressions
  private def expression[_:P]: P[model.Expression] =
    P(name).
      map(model.Expression.Literal.apply)


  // settings

  private def nameSetting[_:P]: P[model.Setting] =
    P("name" ~/ whitespace ~ ":" ~ remainingLine).
      map(model.Setting.Name.apply)

  private def nextSetting[_:P]: P[model.Setting] =
    P("next" ~/ whitespace ~ ":" ~ remainingLine).
      map(model.Setting.Next.apply)

  private def startSetting[_:P]: P[model.Setting] =
    P("start" ~/ whitespace).
      map(_ => model.Setting.Start)

  private def setSetting[_:P]: P[model.Setting] =
    P("set" ~/ whitespace ~ ":" ~ whitespace ~ name ~ whitespace ~ "=" ~
      whitespace ~ expression).
      map(model.Setting.Set.tupled)

  private def testSetting[_:P]: P[model.Setting] =
    P("test" ~/ whitespace ~ ":" ~ whitespace ~ expression).
      map(model.Setting.Test.apply)

  private def successSetting[_:P]: P[model.Setting] =
    P("success" ~/ whitespace ~ ":" ~ whitespace ~ anySetting).
      map(model.Setting.Success.apply)

  private def failureSetting[_:P]: P[model.Setting] =
    P("failure" ~/ whitespace ~ ":" ~ whitespace ~ anySetting).
      map(model.Setting.Failure.apply)

  private def requireSetting[_:P]: P[model.Setting] =
    P("require" ~/ whitespace ~ ":" ~ whitespace ~ expression).
      map(model.Setting.Require.apply)

  private def showAlwaysSetting[_:P]: P[model.Setting] =
    P(("show" ~/ whitespace ~ "always" ~ whitespace) |
      ("always" ~/ whitespace ~ "show" ~ whitespace)).
      map(_ => model.Setting.ShowAlways)

  private def returnSetting[_:P]: P[model.Setting] =
    P("return" ~/ whitespace ~ ":" ~ remainingLine).
      map(model.Setting.Return.apply)

  private def includeSetting[_:P]: P[model.Setting] =
    P("include" ~/ whitespace ~ ":" ~ remainingLine).
      map(model.Setting.Include.apply)



  private def anySetting[_:P]: P[model.Setting] =
    P((nameSetting | nextSetting | startSetting | setSetting |
      testSetting | successSetting | failureSetting | requireSetting |
      showAlwaysSetting | returnSetting | includeSetting))

  private def setting[_:P]: P[Either[String, model.Setting]] =
    P("$" ~/ whitespace ~ anySetting).
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



  def urlnameOf(name: String) =
    name.trim.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase


  private def titleOfContent(content: String): String =
    content.substring(0, content.length.min(20))


  def parseScene(story: model.Story, raw: String):
      Either[String, model.Scene] =
    parsePScene(raw, false).map({ pScene =>
      val title = titleOfContent(pScene.content)
      model.Scene(story.name,
        raw,
        title,
        urlnameOf(title),
        pScene.content,
        pScene.settings)
    })
}

