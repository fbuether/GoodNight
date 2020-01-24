
package goodnight.model.edit


case class Scene(
  story: String, // refers story.urlname
  urlname: String, // refers Scene.urlname
  name: String,
  raw: String,

  prevs: Seq[String], // which scenes refer to this scene
  // which scenes does this scene refer to, and do they already exist
  nexts: Seq[(String, Boolean)])
