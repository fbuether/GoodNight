
package goodnight.common


object ApiV1 {
  sealed trait Segment
  case class C(path: String) extends Segment
  case object S extends Segment // any string without /
  case object R extends Segment

  case class Path(method: String, parts: Segment*) {
    val getPath = this
  }


  private implicit class PathHelper(val sc: StringContext) extends AnyVal {
    def path(args: Any*): Path = sys.error("TODO - IMPLEMENT")
  }


  // matches the string u against a segment s, returning the remaining
  // string if it matches as well as a possibly matched value, otherwise None.
  def matchAndRemainder(s: Segment, u: String):
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
  def matchNext(u: String, sl: Seq[Segment]): Option[Seq[String]] = sl match {
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

  trait MethodExtractor {
    def getPath: Path

    def unapplySeq(req: (String, String)): Option[Seq[String]] = {
      // println(s"*******trying ${req._1} -> ${req._2} for ${getPath} ...")
      if (req._1 == getPath.method) matchNext(req._2, getPath.parts)
      else None
    }
  }

  def writeNext(params: Seq[String], sl: Seq[Segment]): String =
    (params, sl) match {
      case (_, Seq()) => ""
      case (_, C(const) +: sl) => const + writeNext(params, sl)
      case (p +: pl, S +: sl) => p + writeNext(pl, sl)
      case (_, S +: sl) => {
        println("invalid PathCreator call, too few parameters for S!")
        ""
      }
      case (Seq(p), R +: sl) => p + writeNext(Seq(), sl)
      case (_, R +: sl) => {
        println("invalid PathCreator call, too few/many parameters for R!")
        ""
      }
    }

  trait PathCreator {
    def getPath: Path

    def write(params: String*): String = {
      writeNext(params, getPath.parts)
    }
  }


  class ApiPath(method: String, parts: Segment*)
      extends Path(method, parts : _*)
      with MethodExtractor
      with PathCreator


  object Frontend extends ApiPath("GET", C("/"))
  object Asset extends ApiPath("GET", C("/assets/"), R)

  // Xhr Api
  private val p = C("/api/v1/")

  object SignUp extends ApiPath("PUT", p, C("auth"))
  object EmailConfirm extends ApiPath("POST", p, C("auth/confirm"), S)
  object Authenticate extends ApiPath("POST", p, C("auth/authenticate"))
  object SocialAuthenticate
      extends ApiPath("POST", p, C("auth/authenticate/social"), S)
  object SignOut extends ApiPath("DELETE", p, C("auth"))
  object RequestPasswordReset extends ApiPath("POST", p, C("auth/reset"))
  object ConfirmPasswordReset extends ApiPath("POST", p, C("auth/reset"), S)

  object Stories extends ApiPath("GET", p, C("stories"))
  object Story extends ApiPath("GET", p, C("story/"), S)
  object CreateStory extends ApiPath("PUT", p, C("createStory"))

  object Profile extends ApiPath("GET", p, C("profile/"), S)
}
