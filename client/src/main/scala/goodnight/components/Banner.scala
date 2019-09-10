
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl


object Banner {
  case class Props(
    icon: String,
    title: String
  )

  type State = Unit

  def render(p: Props) =
    <.h1(^.className := "banner",
      <.img(^.src := "https://goodnight.jasminefields.net/goodnight/stat/" +
        "images/buuf/" + p.icon),
      <.span(p.title))

  def component =
    ScalaComponent.builder[Props]("Banner").
      stateless.
      noBackend.
      render_P(render).
      build
}
