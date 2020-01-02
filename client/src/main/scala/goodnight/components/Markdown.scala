
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.model.text._
import goodnight.model.text.{ Markdown => TMarkdown }
import goodnight.client.pages
import goodnight.logic.MarkdownParser


object Markdown {
  type Props = (String, Int)

  def ofText(inlines: Inlines): TagMod = inlines.map({
    case Text(text) => TagMod(text, " ")
    case Emph(text) => <.em(text)
    case Strong(text) => <.strong(text)
  }).toTagMod


  def header(level: Int, content: TagMod) = level match {
    case 1 => <.h1(content)
    case 2 => <.h2(content)
    case 3 => <.h3(content)
    case 4 => <.h4(content)
    case 5 => <.h5(content)
    case _ => <.h6(content)
  }

  def ofBlock(b: Block, hOffset: Int): VdomElement = b match {
    case Header(n, text) => header(n + hOffset, ofText(text))
    case Paragraph(text) => <.p(ofText(text))
    case Enum(items) => <.ol(items.map({ case (n,text) =>
      <.li(^.value := n,
        ofBlock(text, hOffset)) }).toTagMod)
    case List(items) => <.ol(items.map(text =>
      <.li(ofBlock(text, hOffset)) ).toTagMod)
    case Blockquote(texts) => <.blockquote(
      texts.foldRight(TagMod())((a,bl) =>
        TagMod(ofText(a), <.br(), bl)))
    case Ruler => <.hr
  }

  val component = ScalaComponent.builder[Props]("Markdown").
    render_PC({ case (props: Props, children: PropsChildren) =>
      MarkdownParser.parse(props._1) match {
        case Left(error) =>
          <.div(^.className := "error",
            <.h2("Markdown Error"),
            <.p("Error: ", error),
            <.p("Source:"),
            <.code(props._1))
        case Right(TMarkdown(blocks)) =>
          <.div(^.className := "markdowned",
            blocks.init.map(ofBlock(_, props._2)).toTagMod,
            (blocks.last match {
              case Paragraph(text) => <.p(ofText(text), children)
              case a => ofBlock(a, props._2) }))
      }

      // // todo: actual markdown parsing and all that.
      // <.p(
      //   text.split("\n").
      //     foldRight(TagMod())((a,bl) => TagMod(a, <.br(), bl)),
      //   children)
    }).
    build
}
