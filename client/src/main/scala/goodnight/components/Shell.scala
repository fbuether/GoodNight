
package goodnight.components

import java.time.Year

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages


object Shell {
  case class Props(
    router: RouterCtl[pages.Page],
    icon: String,
    title: String
  )

  def footer = ScalaComponent.builder.static("Footer")(
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
        ^.title := "on the hunt for the white yonder"))).
    build

  class Backend(bs: BackendScope[Props, Unit]) {
    def render(p: Props, c: PropsChildren): VdomElement =
      <.div(^.className := "goodnight",
        Menu.component(Menu.Props(p.router)),
        Banner.component(Banner.Props(p.icon, p.title)),
        c,
        footer())
  }

  def component = ScalaComponent.builder[Props]("Shell").
    stateless.
    renderBackendWithChildren[Backend].
    componentDidMount(u => Callback(println("shell did monut."))).
    build
}
