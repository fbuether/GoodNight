
package goodnight

package object common {
  // segments are parts of ApiPaths.
  sealed trait Segment
  case class C(path: String) extends Segment // constant string.
  case object S extends Segment // dynamic string without / or until next /
  case object R extends Segment // dynamic string, remainder of url


  private implicit class PathHelper(val sc: StringContext) extends AnyVal {
    def path(args: Any*): Path = sys.error("TODO - IMPLEMENT")
  }
}
