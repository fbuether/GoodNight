
package goodnight.parser

import fastparse._, NoWhitespace._

import goodnight.model
import goodnight.model.text._


object BaseParser {
  private[parser] def whitespace[_:P]: P[Unit] =
    P((" " | "\t").rep(0))

  private[parser] def remainingLine[_:P]: P[String] =
    P(CharsWhile(_ != '\n', 0).!).
      map(_.trim)

  private[parser] def name[_:P]: P[String] =
    P(CharsWhileIn("[a-zA-ZöäüÖÄÜß0-9 ]").!).
      map(_.trim)



  // content

  private[parser] def number[_:P]: P[Int] =
    P(CharIn("0-9").rep(1).!).
      map(_.toInt)

}
