
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.model.text._
import goodnight.model.text.{ Markdown => TMarkdown }
import goodnight.client.pages
import goodnight.logic.MarkdownParser


object Markdown {

  def ofText(inlines: Inlines): TagMod = inlines.map({
    case Text(text) => TagMod(text, " ")
    case Emph(text) => <.em(text)
    case Strong(text) => <.strong(text)
  }).toTagMod

  def ofBlock(b: Block): VdomElement = b match {
    case Header(1, text) => <.h2(ofText(text))
    case Header(2, text) => <.h3(ofText(text))
    case Header(3, text) => <.h4(ofText(text))
    case Header(_, text) => <.h5(ofText(text))
    case Paragraph(text) => <.p(ofText(text))
    case Enum(items) => <.ol(items.map({ case (n,text) =>
      <.li(^.value := n,
        ofBlock(text)) }).toTagMod)
    case List(items) => <.ol(items.map(text =>
      <.li(ofBlock(text)) ).toTagMod)
    case Blockquote(texts) => <.blockquote(
      texts.foldRight(TagMod())((a,bl) =>
        TagMod(ofText(a), <.br(), bl)))
    case Ruler => <.hr
  }

  val component = ScalaComponent.builder[String]("Markdown").
    render_PC({ case (text: String, children: PropsChildren) =>
      MarkdownParser.parse(text) match {
        case Left(error) =>
          <.div(^.className := "error",
            <.h2("Markdown Error"),
            <.p("Error: ", error),
            <.p("Source:"),
            <.code(text))
        case Right(TMarkdown(blocks)) =>
          <.div(^.className := "markdowned",
            blocks.init.map(ofBlock).toTagMod,
            (blocks.last match {
              case Paragraph(text) => <.p(ofText(text), children)
              case a => ofBlock(a) }))
      }

      // // todo: actual markdown parsing and all that.
      // <.p(
      //   text.split("\n").
      //     foldRight(TagMod())((a,bl) => TagMod(a, <.br(), bl)),
      //   children)
    }).
    build
}
