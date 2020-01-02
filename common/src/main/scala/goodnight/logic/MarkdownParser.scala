
package goodnight.logic

import fastparse._, NoWhitespace._

import goodnight.model.text._


object MarkdownParser {

  def anyText[_:P] :P[String] =
    P(CharsWhile(c => c != '\n' && c != '\r', 1).!)

  def text[_:P] :P[String] =
    // c => "\n\r*".indexOf(c) == -1
    P(CharsWhile(c => c != '\n' && c != '\r' && c != '*', 1).!)

  def number[_:P] :P[Int] =
    P(CharIn("0-9").rep(1).!).
      map(_.toInt)



  def emph[_:P] :P[Inline] =
    P("*" ~ text ~ "*").
      map(Emph.apply)

  def strong[_:P] :P[Inline] =
    P("**" ~ text ~ "**").
      map(Strong.apply)

  def markdownLine[_:P] :P[Inlines] =
    P((strong | emph | text.map(Text.apply)).rep(1))



  // interpreters for parts of a line

  def lineWhitespace[_:P] :P[Unit] =
    P((" " | "\t").rep)

  // the remaining text of this line as markuped text.
  // todo: parse markdown.
  def trimmedText[_:P] :P[Inlines] =
    P(lineWhitespace ~ markdownLine ~ lineWhitespace ~ lineEnd)

  def lineBreak[_:P] :P[Unit] =
    P("\n" | "\r\n")

  def lineEnd[_:P] :P[Unit] =
    P(&("\n" | "\r\n" | End))



  // interpreters for single lines


  // Heading lines start with one to six # or = symbols, where the number
  // of symbols denotes the level of the heading.
  def header[_:P] :P[(Int, Inlines)] =
    P(("#" | "=").rep(min=1,max=6).! ~/ trimmedText).
      map({ case (label, text) => (label.length, text) })

  // unordered list items start with a single *, - or +
  def listItem[_:P] :P[Inlines] =
    P(lineWhitespace ~ ("*" | "-" | "+") ~ !("*" | "-" | "+") ~/ trimmedText)

  // rulers are a line with only - or = or *, at least three.
  def ruler[_:P] :P[Unit] =
    P(lineWhitespace ~ ("*" | "-" | "=").rep(3) ~/ lineWhitespace ~ lineEnd)

  // blockquotes start with > and contain only plain text.
  def blockquote[_:P] :P[Inlines] =
    P(">" ~/ trimmedText)

  // ordered list items start with a number, followed by a dot.
  def enumItem[_:P] :P[(Int, Inlines)] =
    P(lineWhitespace ~ number ~ "." ~/ trimmedText)

  // indentedLines start with two or more spaces to denote a subblock or
  // a continuation of e.g. a list item.
  def indentedLine[_:P] :P[Inlines] =
    P(" ".rep(2) ~/ trimmedText)

  // plain text lines do not have a specific symbol.
  def plainLine[_:P] :P[Inlines] =
    P(trimmedText)

  def emptyLine[_:P] :P[Unit] =
    P(lineEnd)


  // def line[_:P] :P[Inline] =
  //   P(ruler | header | listItem | enumItem | blockquote |
  //     indentedLine | plainLine | emptyLine
  //   )




  // blocks.
  def list[_:P] :P[Block] =
    P((listItem ~
      (lineBreak ~ indentedLine.rep(min=1, sep=lineBreak)).?).
      rep(1, sep=lineBreak)).
      map(items => List(items.map({ case (a,al) =>
        Paragraph(a ++ al.getOrElse(Seq()).flatten) })))

  def enum[_:P] :P[Block] =
    P((enumItem ~
      (lineBreak ~ indentedLine.rep(min=1, sep=lineBreak)).?).
      rep(1, sep=lineBreak)).
      map(items => Enum(items.map({ case (n,a,al) =>
        (n, Paragraph(a ++ al.getOrElse(Seq()).flatten)) })))

  def blockquoteBlock[_:P] :P[Block] =
    P(blockquote.rep(1, sep = lineBreak)).
      map(texts => Blockquote(texts))

  def block[_:P] :P[Block] =
    P(ruler.map(_ => Ruler) |
      header.map({ case (level, content) => Header(level, content) }) |
      list |
      enum |
      blockquoteBlock)


  def paragraph[_:P] :P[Block] =
    P(plainLine.rep(1, sep = lineBreak)).
      map(lines => Paragraph(lines.flatten))


  def blocks[_:P] :P[Blocks] =
    P((block | paragraph).rep(sep = lineBreak.rep(1)))



  // def blocks[_:P] :P[Blocks] =
  //   P(block.rep(sep = lineBreak./))



  def markdown[_:P]: P[Markdown] =
    P(Start ~ lineBreak.rep ~ blocks ~ lineBreak.rep ~ End).
      map(Markdown.apply)


  def parse(raw: String): Either[String, Markdown] =
    fastparse.parse(raw, markdown(_), verboseFailures = true) match {
      case Parsed.Success(value, _) => Right(value)
      case failure @ Parsed.Failure(_,_,_) =>
        Left(failure.trace().longAggregateMsg)
    }

}
