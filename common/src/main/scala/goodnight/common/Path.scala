
package goodnight.common




case class Path(method: String, parts: Segment*) {
  val getPath: Path = this
}
