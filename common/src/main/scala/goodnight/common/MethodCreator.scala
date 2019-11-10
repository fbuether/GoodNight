
package goodnight.common


trait MethodExtractor {
  def getPath: Path

  // matches the string u against a segment s, returning the remaining
  // string if it matches as well as a possibly matched value, otherwise None.
  private def matchAndRemainder(s: Segment, u: String):
      Option[(String, Option[String])] = s match {
    case C(const) => {
      // println(s"******does $u start with $const? ${u.startsWith(const)}")
      if (u.startsWith(const)) Some((u.substring(const.length), None))
      else None
    }
    case S => u.indexOf('/') match {
      case -1 => Some("", Some(u))
      case i =>
        // println(s"********matching string of $u -> ${u.substring(i)} ~> ${u.substring(0,i)}")
        Some(u.substring(i), Some(u.substring(0, i)))
    }
    case R => Some("", Some(u))
  }


  // matches u against the segments in sl, until either everything has been
  // matched or a mismatch occurs. If everything works, returns a list of
  // the matched parts.
  private def matchNext(u: String, sl: Seq[Segment]): Option[Seq[String]] =
    sl match {
      case Seq() =>
        if (u == "") Some(Seq())
        else None
      case segment +: sl => {
        matchAndRemainder(segment, u) match {
          case None => None
          case Some((urem, m)) => {
            matchNext(urem, sl) match {
              case None => None
              case Some(matches) => m match {
                case Some(v) => Some(v +: matches)
                case None => Some(matches)
              }
            }
          }
        }
      }
    }



  def unapplySeq(req: (String, String)): Option[Seq[String]] = {
    // println(s"*******trying ${req._1} -> ${req._2} for ${getPath} ...")
    if (req._1 == getPath.method) matchNext(req._2, getPath.parts)
    else None
  }
}
