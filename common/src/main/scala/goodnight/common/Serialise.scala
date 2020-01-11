
package goodnight.common

import upickle.default
import upickle.default.ReadWriter
import upickle.default.macroRW
import ujson.ParseException

import goodnight.model


object Serialise {
  type Serialisable[A] = ReadWriter[A]

  implicit val serialise_activity: Serialisable[model.Activity] = macroRW

  implicit val serialise_expression_unaryoperator:
      Serialisable[model.Expression.UnaryOperator] =
    ReadWriter.merge(
      macroRW[model.Expression.Not.type],
      macroRW[model.Expression.PlusOne.type])

  implicit val serialise_expression_binaryoperator:
      Serialisable[model.Expression.BinaryOperator] =
    ReadWriter.merge(
      macroRW[model.Expression.Add.type],
      macroRW[model.Expression.Sub.type],
      macroRW[model.Expression.Mult.type],
      macroRW[model.Expression.Div.type],
      macroRW[model.Expression.And.type],
      macroRW[model.Expression.Or.type],
      macroRW[model.Expression.Greater.type],
      macroRW[model.Expression.GreaterOrEqual.type],
      macroRW[model.Expression.Less.type],
      macroRW[model.Expression.LessOrEqual.type],
      macroRW[model.Expression.Equal.type],
      macroRW[model.Expression.NotEqual.type])

  implicit val serialise_expression: Serialisable[model.Expression] =
    ReadWriter.merge(
      macroRW[model.Expression.Quality],
      macroRW[model.Expression.Literal],
      macroRW[model.Expression.Random],
      macroRW[model.Expression.Unary],
      macroRW[model.Expression.Binary])

  implicit val serialise_player: Serialisable[model.Player] = macroRW

  implicit val serialise_sort: Serialisable[model.Sort] =
    ReadWriter.merge(
      macroRW[model.Sort.Boolean.type],
      macroRW[model.Sort.Enumeration],
      macroRW[model.Sort.Integer])

  implicit val serialise_quality: Serialisable[model.Quality] = macroRW

  implicit val serialise_setting: Serialisable[model.Setting] =
    ReadWriter.merge(
      macroRW[model.Setting.Name],
      macroRW[model.Setting.Next],
      macroRW[model.Setting.Start.type],
      macroRW[model.Setting.Set],
      macroRW[model.Setting.Test],
      macroRW[model.Setting.Success],
      macroRW[model.Setting.Failure],
      macroRW[model.Setting.Require],
      macroRW[model.Setting.ShowAlways.type],
      macroRW[model.Setting.Return],
      macroRW[model.Setting.Include])

  implicit val serialise_scene: Serialisable[model.Scene] = macroRW

  implicit val serialise_sceneview: Serialisable[model.SceneView] = macroRW
  implicit val serialise_nextscene: Serialisable[model.NextScene] = macroRW
  implicit val serialise_Requirement: Serialisable[model.Requirement] = macroRW

  implicit val serialise_story: Serialisable[model.Story] = macroRW

  implicit val serialise_user: Serialisable[model.User] = macroRW


  // serialise model.play

  implicit val serialise_play_activity: Serialisable[model.read.Activity] = macroRW

  implicit val serialise_play_choice: Serialisable[model.read.Choice] = macroRW

  implicit val serialise_play_player: Serialisable[model.read.Player] = macroRW

  implicit val serialise_play_quality: Serialisable[model.read.Quality[model.read.Sort]] =
    macroRW
  implicit val serialise_play_quality_b:
      Serialisable[model.read.Quality[model.read.Sort.Bool.type]] = macroRW
  implicit val serialise_play_quality_i:
      Serialisable[model.read.Quality[model.read.Sort.Integer.type]] = macroRW

  implicit val serialise_play_scene: Serialisable[model.read.Scene] = macroRW

  implicit val serialise_play_sort: Serialisable[model.read.Sort] =
    ReadWriter.merge(
      macroRW[model.read.Sort.Bool.type],
      macroRW[model.read.Sort.Integer.type])

  implicit val serialise_play_state: Serialisable[model.read.State] =
    ReadWriter.merge(
      macroRW[model.read.State.Bool],
      macroRW[model.read.State.Integer])

  implicit val serialise_play_story: Serialisable[model.read.Story] = macroRW

  implicit val serialise_play_test: Serialisable[model.read.Test] =
    ReadWriter.merge(
      macroRW[model.read.Test.Bool],
      macroRW[model.read.Test.Integer])



  def write[A](a: A)(implicit rw: Serialisable[A]): String =
    default.write(a)

  def read[A](input: String)(implicit rw: Serialisable[A]): A =
    default.read[A](input)

  def readMaybe[A](input: String)(implicit rw: Serialisable[A]): Option[A] =
    try Some(default.read[A](input))
    catch { case (_: ParseException) => None }
}
