
package goodnight.model.text



sealed trait BlockElement
// case class Heading(level: Int, content: Seq[InlineElement]) extends BlockElement
// case class Paragraph(content: Seq[InlineElement]) extends BlockElement
// // case class Enumeration(items: Seq[BlockElement]) extends BlockElement
// // case class List(items: Seq[BlockElement]) extends BlockElement
// // case class Blockquote(content: BlockElement) extends BlockElement

// sealed trait InlineElement extends Markdown
// case class Text(text: String) extends InlineElement
// case class Emphasis(text: InlineElement) extends InlineElement
// case class Strong(text: InlineElement) extends InlineElement
// case object Linebreak extends InlineElement




case class Paragraph(content: String) extends BlockElement



case class Markdown(elements: Seq[BlockElement])
