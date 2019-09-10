
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
    title: String
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
    def render(p: Props, s: State, c: PropsChildren): VdomElement =
      <.div(^.className := "central",
        Menu.component(Menu.Props(p.router)),
        Banner.component(Banner.Props(p.icon, p.title)),
        c,
        footer)
  }

  def component =
    ScalaComponent.builder[Props]("Shell").
      stateless.
      renderBackendWithChildren[Backend].
      configure(Reusability.shouldComponentUpdateAndLog("shell")).
      componentDidMount(u => Callback(
        println("shell did monut."))).
      build
}
