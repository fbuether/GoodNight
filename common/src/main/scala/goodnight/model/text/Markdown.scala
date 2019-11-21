
package goodnight.model.text



sealed trait Markdown

case class Sequence(elements: Seq[Markdown]) extends Markdown

case class Paragraph(content: Seq[Text]) extends Markdown

sealed trait Text extends Markdown

case class Plain(content: String) extends Text

case class Bold(content: String) extends Text

case class Heading(content: Text) extends Markdown
