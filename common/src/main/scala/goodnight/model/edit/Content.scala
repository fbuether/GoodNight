
package goodnight.model.edit


case class Content(
  story: String, // refers story.urlname
  scenes: Seq[SceneHeader],
  qualities: Seq[QualityHeader])
