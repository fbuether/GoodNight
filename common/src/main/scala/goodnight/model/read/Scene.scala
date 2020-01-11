
package goodnight.model.read


case class Scene(
  story: String, // refers Story.urlname
  urlname: String, // refers edit.Scene.urlname
  text: String, // the text shown as this story, markdown
  // returnsTo: Option[String], // if this scene has a return, its urlname
  choices: Seq[Choice]) // all options available to the current player
