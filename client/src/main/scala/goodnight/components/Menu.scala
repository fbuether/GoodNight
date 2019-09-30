
package goodnight.components

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.extra.Listenable
import japgolly.scalajs.react.extra.Broadcaster

import goodnight.client.pages
import goodnight.service.User
import goodnight.service.AuthenticationService


object Menu {
  type Props = RouterCtl[pages.Page]

  type State = Option[User]

  class Backend(bs: BackendScope[Props, State]) extends OnUnmount {
    val menuRef = Ref[html.Div]

    def toggleExpandedMenu = menuRef.foreach({ menu =>
      val cn = menu.className
      if (cn.endsWith("expanded"))
        menu.className = cn.substring(0, cn.indexOf("expanded") - 1)
      else
        menu.className = cn + " expanded"
    })

    def item(router: RouterCtl[pages.Page], page: pages.Page, icon: String,
      title: String, linkClass: String = "") =
      <.li(^.className := linkClass,
        router.link(page)(
          <.span(^.className := icon),
          " " + title))

    def doSignOut(router: RouterCtl[pages.Page]): Callback = {
      AuthenticationService.removeAuthentication >>
      router.set(pages.Home)
    }

    def render(p: Props, user: State) = {
      val userItems = user match {
        case None => Seq(
          item(p, pages.Register, "far fa-bookmark", "Register"),
          // this span is required to have react create new elements.
          // otherwise, after logout, the new sign in link would also fire.
          <.span(),
          item(p, pages.SignIn, "far fa-check-square", "Sign in"))
        case Some(User(name)) => Seq(
          item(p, pages.Profile, "fa fa-user-astronaut", name),
          <.li(<.a(^.onClick --> doSignOut(p),
            <.span(^.className := "far fa-times-circle"),
            " Sign out")))
      }

      <.div.withRef(menuRef)(^.className := "menu",
        <.ul(
          item(p, pages.Home, "far fa-moon", "GoodNight", "header"),
          item(p, pages.Stories, "far fa-file-alt", "Stories"),
          item(p, pages.Community, "far fa-comment", "Community")),
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

  val component = ScalaComponent.builder[Props]("Menu").
    initialStateCallback(getUsername).
    renderBackend[Backend].
    configure(Listenable.listen(_ => AuthenticationService.LoginEvents,
      cmu => cmu.setState)).
    build
}
