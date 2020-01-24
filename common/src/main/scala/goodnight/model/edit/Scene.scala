
package goodnight.model.edit


case class Scene(
  story: String, // refers story.urlname
  urlname: String, // refers Scene.urlname
  name: String,
  raw: String,
  refers: Seq[String], // which scenes refer to this scene
  nexts: Seq[String]) // which scenes does this refer to
