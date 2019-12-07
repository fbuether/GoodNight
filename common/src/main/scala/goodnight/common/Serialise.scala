
package goodnight.common

import upickle.default
import upickle.default.ReadWriter
import upickle.default.macroRW
import ujson.ParseException

import goodnight.model


object Serialise {
  type Serialisable[A] = ReadWriter[A]

  implicit val serialise_location: Serialisable[model.Location] = macroRW

  implicit val serialise_state_location: Serialisable[model.State.Location] =
    macroRW
  implicit val serialise_state_scene: Serialisable[model.State.Scene] = macroRW
  implicit val serialise_state_choice: Serialisable[model.State.Choice] =
    macroRW
  implicit val serialise_state: Serialisable[model.State] =
    ReadWriter.merge(serialise_state_location, serialise_state_scene,
      serialise_state_choice)
  implicit val serialise_player: Serialisable[model.Player] = macroRW

  implicit val serialise_choice: Serialisable[model.Choice] = macroRW
  implicit val serialise_scene: Serialisable[model.Scene] = macroRW

  implicit val serialise_story: Serialisable[model.Story] = macroRW

  implicit val serialise_user: Serialisable[model.User] = macroRW


  def write[A](a: A)(implicit rw: Serialisable[A]): String =
    default.write(a)

  def read[A](input: String)(implicit rw: Serialisable[A]): A =
    default.read[A](input)

  def readMaybe[A](input: String)(implicit rw: Serialisable[A]): Option[A] =
    try Some(default.read[A](input))
    catch { case (_: ParseException) => None }
}
