
package goodnight.model.edit


case class Scene(
  story: String, // refers story.urlname
  urlname: String, // refers Scene.urlname

  name: String,
  textHead: String, // the first couple of chars of the actual text
  text: String)
