
package goodnight.model.read


case class Choice(
  urlname: String, // refers Scene.urlname
  text: String, // the text shown as this choice, markdown
  available: Boolean, // can this choice be taken?
  // order: Int, // the order in which choices should be shown
  tests: Seq[Test]) // the tests required to take this choice
