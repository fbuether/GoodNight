
package goodnight.model


// this is a scene as it is read by a player.
// editing uses Scene, but playing operates on SceneViews.

case class SceneView(
  story: String, // refers Scene.story
  urlname: String, // refers Scene.urlname
  text: String, // the text shown as this stories body, markdown
  hasReturn: Boolean, // true if a return-to-previous-scene-button should exist
  choices: Seq[NextScene])

case class NextScene(
  urlname: String, // refers Scene.urlname
  text: String, // the text shown as a choice, markdown
  tests: Seq[Requirement])

case class Requirement(
  quality: Quality,
  minimum: Int, // a minimum value that the player must have
  hasMin: Boolean, // has the player this amount?
  chance: Double) // a change of success. 1 means a sure success
