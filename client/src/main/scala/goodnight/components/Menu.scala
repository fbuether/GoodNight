
package goodnight.components

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import japgolly.scalajs.react.StateAccess.ModStateWithProps
import japgolly.scalajs.react.component.builder.Lifecycle.ComponentWillMount

import goodnight.client.pages
import goodnight.service.User
import goodnight.service.AuthenticationService


object Menu {
  type Props = (RouterCtl[pages.Page])

  type State = (Option[User])

  class Backend(bs: BackendScope[Props, State]) {
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

    def doSignOut: Callback = Callback {
      println("signing out.")
    }

    def render(router: RouterCtl[pages.Page], user: State) = {
      val userItems = user match {
        case None =>
          Seq(item(router, pages.Register, "bookmark-o", "Register"),
            item(router, pages.SignIn, "check-square-o", "Sign in"))
        case Some(User(name)) =>
          Seq(item(router, pages.Profile, "sun-o", name),
            <.li(<.a(^.onClick --> doSignOut,
              <.span(^.className := "fa fa-times-circle-o"),
              " Sign out")))
      }

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
          userItems.toTagMod))
    }
  }


  def getUsername: CallbackTo[State] =
    AuthenticationService.getUser

  // def fetchLoginInfo(cwu: // ComponentWillMount[Props, Unit, Backend]
  //     ModStateWithProps[CallbackTo, Props, State]
  // ) =
  //   AuthenticationService.isLoggedIn.flatMap(valid => Callback {

  //     println("menu for logged in " + valid)
  //   })

  val component = ScalaComponent.builder[Props]("Menu").
    initialStateCallback(getUsername).
    renderBackend[Backend].
    build
}
