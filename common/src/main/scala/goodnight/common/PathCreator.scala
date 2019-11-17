
package goodnight.common


trait PathCreator {
  def getPath: Path

  private def writeNext(params: Seq[String], sl: Seq[Segment]): String =
    (params, sl) match {
      case (_, Seq()) => ""
      case (_, C(const) +: sl) => const + writeNext(params, sl)
      case (p +: pl, S +: sl) => p + writeNext(pl, sl)
      case (_, S +: sl) => {
        throw new Error("invalid PathCreator call, too few parameters for S!")
      }
      case (Seq(p), R +: sl) => p + writeNext(Seq(), sl)
      case (_, R +: sl) => {
        throw new Error(
          "invalid PathCreator call, too few/many parameters for R!")
      }
    }

  def write(params: String*): String = {
    writeNext(params, getPath.parts)
  }
}

