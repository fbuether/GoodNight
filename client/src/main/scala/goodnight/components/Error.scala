
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._


object Error {
  val component = ScalaComponent.builder[(Throwable, Boolean)]("Error").
    render_P({ case (error, overlay) =>
      <.div(^.className := "error" + (if (overlay) " overlay" else ""),
        <.div(^.className := "title",
          "An error occurred:"),
        error.getMessage)
    }).
    build
}
