
package goodnight.components

import java.time.Year

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.Pages


object Shell {
  case class Props(
    router: RouterCtl[Pages.Page],
    icon: String,
    title: String,
    content: VdomElement
  )

  type State = Unit

  def footer =
    <.div(^.className := "schlussvermerk",
      "© " + Year.now.getValue + " ",
      <.a(^.href := "https://jasminefields.net",
        ^.title := "jasminéfields.net",
        ^.target := "_blank",
        "jasminéfields.net"),
      " ~ ",
      // about
      // mailto
      <.img(^.src := "https://goodnight.jasminefields.net/goodnight/" +
        "stat/images/othftwy.gif",
        ^.title := "on the hunt for the white yonder"))


  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props, s: State): VdomElement =
      <.div(^.className := "central",
        Menu.component(Menu.Props(p.router)),
        <.h1(^.className := "banner",
          <.img(^.src := "https://goodnight.jasminefields.net/goodnight/stat/" +
            "images/buuf/" + p.icon),
          <.span(p.title)),
        p.content,
        footer)
  }

  def component =
    ScalaComponent.builder[Props]("Shell").
      stateless.
      // initialState(List("hello", "snazzy")).
      renderBackend[Backend].
      build
}
