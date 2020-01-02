
package goodnight.model.text


sealed trait Block
case class Header(level: Int, content: Inlines) extends Block
case class Paragraph(content: Inlines) extends Block
case class Enum(items: Seq[(Int, Block)]) extends Block
case class List(items: Blocks) extends Block
case class Blockquote(content: Seq[Inlines]) extends Block
case object Ruler extends Block
// case class Image(path: String) extends Block


sealed trait Inline
case class Text(text: String) extends Inline
case class Emph(text: String) extends Inline
case class Strong(text: String) extends Inline


case class Markdown(elements: Blocks)
