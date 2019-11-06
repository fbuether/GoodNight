
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


object Error {
  val component = ScalaComponent.builder[Throwable]("Error").
    render_P({ error =>
      <.div(^.className := "error",
        <.div(^.className := "title",
          "An error occurred:"),
        error.getMessage)
    }).
    build
}
