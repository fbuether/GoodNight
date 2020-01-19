
package goodnight.model.edit


case class Content(
  story: Story,
  scenes: Seq[SceneHeader],
  qualities: Seq[QualityHeader])
