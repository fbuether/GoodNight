
package goodnight.components

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages


object Menu {
  case class Props(
    router: RouterCtl[pages.Page]
  )

  class Backend(bs: BackendScope[Props, Unit]) {
    val menuRef = Ref[html.Div]

    def expandMenu: Callback =
      menuRef.foreach({ menu =>
        val cn = menu.className
        if (cn.endsWith("expanded"))
          menu.className = cn.substring(0, cn.indexOf("expanded") - 1)
        else
          menu.className = cn + " expanded"
      })

    def render(p: Props): VdomElement =
      <.div.withRef(menuRef)(^.className := "menu",
        <.ul(
          <.li(^.className := "header",
            p.router.link(pages.Home)(
              <.span(^.className := "fa fa-moon-o"),
              " GoodNight")),
          <.li(
            p.router.link(pages.Worlds)(
              <.span(^.className := "fa fa-globe"),
              " Worlds")),
          <.li(
            p.router.link(pages.Community)(
              <.span(^.className := "fa fa-comment-o"),
              " Community"))
        ),
        <.ul(
          <.li(^.className := "expander",
            <.a(^.onClick --> expandMenu,
              <.span(^.className := "fa fa-navicon"),
              " Menu")),
          <.li(
            p.router.link(pages.Register)(
              <.span(^.className := "fa fa-bookmark-o"),
              " Register")),
          <.li(
            p.router.link(pages.SignIn)(
              <.span(^.className := "fa fa-check-square-o"),
              " Sign in"))))
  }

  def component = ScalaComponent.builder[Props]("Menu").
    stateless.
    renderBackend[Backend].
    build
}
