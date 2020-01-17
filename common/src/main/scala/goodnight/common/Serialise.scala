
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
      macroRW[model.Expression.Not.type])

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
      macroRW[model.Expression.Text],
      macroRW[model.Expression.Number],
      // macroRW[model.Expression.Random],
      macroRW[model.Expression.Unary],
      macroRW[model.Expression.Binary])

  implicit val serialise_player: Serialisable[model.Player] = macroRW

  implicit val serialise_sort: Serialisable[model.Sort] =
    ReadWriter.merge(
      macroRW[model.Sort.Boolean.type],
      // macroRW[model.Sort.Enumeration],
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


  // serialise model.read

  implicit val serialise_read_activity: Serialisable[model.read.Activity] =
    macroRW

  implicit val serialise_read_choice: Serialisable[model.read.Choice] = macroRW

  implicit val serialise_read_player: Serialisable[model.read.Player] = macroRW

  implicit val serialise_read_quality_bool:
      Serialisable[model.read.Quality.Bool] = macroRW
  implicit val serialise_read_quality_integer:
      Serialisable[model.read.Quality.Integer] = macroRW
  implicit val serialise_read_quality: Serialisable[model.read.Quality] =
    ReadWriter.merge(serialise_read_quality_bool,
      serialise_read_quality_integer)

  implicit val serialise_read_scene: Serialisable[model.read.Scene] = macroRW

  implicit val serialise_read_sort: Serialisable[model.read.Sort] =
    ReadWriter.merge(
      macroRW[model.read.Sort.Bool.type],
      macroRW[model.read.Sort.Integer.type])

  implicit val serialise_read_state_bool:
      Serialisable[model.read.State.Bool] = macroRW
  implicit val serialise_read_state_integer:
      Serialisable[model.read.State.Integer] = macroRW
  implicit val serialise_read_state: Serialisable[model.read.State] =
    ReadWriter.merge(serialise_read_state_bool,
      serialise_read_state_integer)

  implicit val serialise_read_story: Serialisable[model.read.Story] = macroRW

  implicit val serialise_read_test: Serialisable[model.read.Test] = macroRW


  // serialise model.edit

  implicit val serialise_edit_Content: Serialisable[model.edit.Content] =
    macroRW

  implicit val serialise_edit_QualityHeader:
      Serialisable[model.edit.QualityHeader] = macroRW

  implicit val serialise_edit_Quality: Serialisable[model.edit.Quality] =
    macroRW

  implicit val serialise_edit_SceneHeader:
      Serialisable[model.edit.SceneHeader] = macroRW

  implicit val serialise_edit_Scene: Serialisable[model.edit.Scene] = macroRW

  implicit val serialise_edit_Story: Serialisable[model.edit.Story] = macroRW





  def write[A](a: A)(implicit rw: Serialisable[A]): String =
    default.write(a)

  def read[A](input: String)(implicit rw: Serialisable[A]): A =
    default.read[A](input)

  def readMaybe[A](input: String)(implicit rw: Serialisable[A]): Option[A] =
    try Some(default.read[A](input))
    catch { case (_: ParseException) => None }
}
