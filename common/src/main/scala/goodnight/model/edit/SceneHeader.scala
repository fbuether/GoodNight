
package goodnight.model.edit


case class SceneHeader(
  urlname: String, // refers Scene.urlname

  name: String,
  textHead: String, // the first couple of chars of the actual text
  start: Boolean)
