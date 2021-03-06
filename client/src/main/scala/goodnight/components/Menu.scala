
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.Serialise._
import goodnight.model.User
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._
import goodnight.service.{ Request, Reply }


object Menu {
  type Props = pages.Router

  type State = Option[User]

  class Backend(bs: BackendScope[Props, State]) {
    def initialise = Callback(AuthenticationService.onUserChange(
      bs.setState(_).runNow))

    def doSignOut(router: pages.Router): Callback =
      AuthenticationService.signOut >>
        router.set(pages.Home)

    val menuRef = Ref[html.Div]

    def toggleExpandedMenu = menuRef.foreach({ menu =>
      val cn = menu.className
      if (cn.endsWith("expanded"))
        menu.className = cn.substring(0, cn.indexOf("expanded") - 1)
      else
        menu.className = cn + " expanded"
    })

    def item(router: pages.Router, page: pages.Page, icon: String,
      title: String, linkClass: String = "") =
      <.li(^.className := linkClass,
        router.link(page)(
          <.span(^.className := icon),
          title))

    def render(p: Props, user: State) = {
      val userItems = user match {
        case None => Seq(
          item(p, pages.Register, "far fa-bookmark", "Register"),
          // this span is required to have react create new elements.
          // otherwise, after logout, the new sign in link would also fire.
          <.span(),
          item(p, pages.SignIn, "fas fa-sign-in-alt", "Sign in"))
        case Some(user) => Seq(
          item(p, pages.Profile, "fa fa-user-astronaut", user.name),
          <.li(<.a(^.onClick --> doSignOut(p),
            <.i(^.className := "fas fa-sign-out-alt"),
            "Sign out")))
      }

      <.div.withRef(menuRef)(^.className := "menu",
        <.ul(
          item(p, pages.Home, "far fa-moon", "GoodNight", "header"),
          item(p, pages.Stories, "far fa-file-alt", "Stories"),
          item(p, pages.Community, "far fa-comment", "Community")),
        <.ul(
          <.li(^.className := "expander",
            <.a(^.onClick --> toggleExpandedMenu,
              <.i(^.className := "fa fa-navicon"),
              "Menu")),
          userItems.toTagMod))
    }
  }

  val component = ScalaComponent.builder[Props]("Menu").
    initialState[State](None).
    renderBackend[Backend].
    componentWillMount(_.backend.initialise).
    build
}
