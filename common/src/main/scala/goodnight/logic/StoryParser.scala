
package goodnight.logic

import fastparse._, NoWhitespace._
import scala.util.{Try, Success, Failure}


import goodnight.model
import goodnight.model.text._


object StoryParser {

  private def line[_:P]: P[String] = P(CharsWhile(_ != '\n').! ~ ("\n" | End))

  private def plainLine[_:P] : P[Markdown] = P(line.map(Plain.apply))

  private def heading[_:P]: P[Markdown] =
    P("#".rep(1) ~ !"#" ~ line.!).map(s => Heading(Plain(s)))

  private def full[_:P]: P[Markdown] =
    P((heading | plainLine).rep.map(Sequence) ~ End)


  def parse(rawStory: String): Parsed[Markdown] =
    fastparse.parse(rawStory, full(_))
}



