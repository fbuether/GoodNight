
package goodnight.common

import upickle.default
import upickle.default.ReadWriter
import upickle.default.macroRW

import goodnight.model



object Serialise {
  implicit val location: ReadWriter[model.Location] = macroRW
  implicit val player: ReadWriter[model.Player] = macroRW
  implicit val story: ReadWriter[model.Story] = macroRW
  implicit val user: ReadWriter[model.User] = macroRW

  def write[A](a: A)(implicit rw: ReadWriter[A]): String =
    default.write(a)
  def read[A](input: String)(implicit rw: ReadWriter[A]): A =
    default.read[A](input)
}
