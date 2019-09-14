
package goodnight.components

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages


object Menu {
  type Props = (RouterCtl[pages.Page])

  class Backend(bs: BackendScope[Props, Unit]) {
    val menuRef = Ref[html.Div]

    def toggleExpandedMenu = menuRef.foreach({ menu =>
      val cn = menu.className
      if (cn.endsWith("expanded"))
        menu.className = cn.substring(0, cn.indexOf("expanded") - 1)
      else
        menu.className = cn + " expanded"
    })

    def item(router: RouterCtl[pages.Page],
      page: pages.Page, icon: String, title: String,linkClass: String = "") =
      <.li(^.className := linkClass,
        router.link(page)(
          <.span(^.className := "fa fa-" + icon),
          " " + title))

    def render(router: RouterCtl[pages.Page]) =
      <.div.withRef(menuRef)(^.className := "menu",
        <.ul(
          item(router, pages.Home, "moon-o", "GoodNight", "header"),
          item(router, pages.Worlds, "globe", "Worlds"),
          item(router, pages.Community, "comment-o", "Community")),
        <.ul(
          <.li(^.className := "expander",
            <.a(^.onClick --> toggleExpandedMenu,
              <.span(^.className := "fa fa-navicon"),
              " Menu")),
          item(router, pages.Register, "bookmark-o", "Register"),
          item(router, pages.SignIn, "check-square-o", "Sign in")))
  }

  val component = ScalaComponent.builder[Props]("Menu").
    renderBackend[Backend].
    build
}
