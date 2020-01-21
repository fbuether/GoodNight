
package goodnight.stories.read

import scala.util.{ Try, Success, Failure }

import goodnight.db
import goodnight.model
import goodnight.printer.ExpressionPrinter


object Convert {
  private val invalidDefaultQuality =
    model.read.Quality.Bool("invalid story",
    "invalid quality", "invalid quality", "X.png")


  def readPlayer(player: db.model.Player): model.read.Player =
    model.read.Player(player.user, player.story, player.name)


  def readState(quality: model.read.Quality, value: String):
      model.read.State =
    quality match {
      case quality @ model.read.Quality.Bool(_,_,_,_) =>
        model.read.State.Bool(quality, value == "true")
      case quality @ model.read.Quality.Integer(_,_,_,_) =>
        model.read.State.Integer(quality, Try(value.toInt).getOrElse(0)) }

  def readState(qualities: Seq[db.model.Quality], state: db.model.State):
      model.read.State =
    qualities.find(_.urlname == state.quality).map(readQuality) match {
      case Some(quality) => readState(quality, state.value)
      case None => model.read.State(invalidDefaultQuality, false) }

  private def readQuality(qualities: Seq[db.model.Quality],
    qualityName: String): model.read.Quality =
    qualities.find(_.urlname == qualityName).map(readQuality).
      getOrElse(invalidDefaultQuality)


  def readQuality(quality: db.model.Quality): model.read.Quality =
    quality.sort match {
      case db.model.Sort.Bool =>
        model.read.Quality.Bool(quality.story,
          quality.urlname, quality.name, quality.image)
      case db.model.Sort.Integer =>
        model.read.Quality.Integer(quality.story,
          quality.urlname, quality.name, quality.image) }


  def readActivity(qualities: Seq[db.model.Quality],
    activity: db.model.Activity, effects: Seq[db.model.State]):
      model.read.Activity =
    model.read.Activity(activity.story,
      activity.user,
      activity.scene,
      effects.map(readState(qualities, _)))


  private def getFirstQuality(expr: model.Expression): Option[String] =
    expr match {
      case model.Expression.Text(n) => Some(n)
      case model.Expression.Number(_) => None
      case model.Expression.Unary(_, e) => getFirstQuality(e)
      case model.Expression.Binary(_, e1, e2) =>
        getFirstQuality(e1).orElse(getFirstQuality(e2))
    }


  def readTest(qualities: Seq[db.model.Quality],
    state: Seq[(db.model.State, db.model.Quality)],
    expr: model.Expression): model.read.Test =
    model.read.Test(
      readQuality(qualities, getFirstQuality(expr).getOrElse("invalid")),
      Expression.evaluate(state, qualities, expr) match {
        case Some(model.Expression.Value.Bool(v)) => v
        case Some(model.Expression.Value.Integer(i)) => i > 0
        case _ => false
      },
      ExpressionPrinter.toTest(expr))
}
