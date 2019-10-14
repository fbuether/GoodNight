
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import scala.util.{ Try, Success, Failure }

import goodnight.client.pages
import goodnight.common.ApiV1
import goodnight.common.api.User._
import goodnight.model.User
import goodnight.service.AuthenticationService
import goodnight.service.Conversions._
import goodnight.service.Storage
import goodnight.service.{ Request, Reply }


object Menu {
  type Props = pages.Router

  type State = Option[User]

  class Backend(bs: BackendScope[Props, State]) {
    def loadUser: Callback = {
      CallbackTo(Storage.get[User]("user")).flatMap({
        case Some(user) =>
          bs.setState(Some(user))
        case None =>
          CallbackTo(Storage.get[String]("auth-token")).flatMap({
            case None => Callback(())
            case Some(token) => Callback({
              println("only got a token. request user?")
            }).flatMap(_ => {
              Request.get(ApiV1.Self).send.forJson.flatMap({
                case Reply(200, Success(userJson)) =>
                  val user = userJson.as[User]
                  println("got user " + userJson)
                  Storage.set("user", user)
                  bs.setState(Some(user)).async
                case r =>
                  Callback({
                    println("got bad reply :( => " + r)
                  }).async
              }).toCallback
            })
          })
      })
    }

    def doSignOut(router: pages.Router): Callback = {
      AuthenticationService.removeAuthentication >>
      router.set(pages.Home)
    }

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
          " " + title))

    def render(p: Props, user: State) = {
      println(s"rendering menu from state $user")
      val userItems = user match {
        case None => Seq(
          item(p, pages.Register, "far fa-bookmark", "Register"),
          // this span is required to have react create new elements.
          // otherwise, after logout, the new sign in link would also fire.
          <.span(),
          item(p, pages.SignIn, "far fa-check-square", "Sign in"))
        case Some(user) => Seq(
          item(p, pages.Profile, "fa fa-user-astronaut", user.name),
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

  val component = ScalaComponent.builder[Props]("Menu").
    initialState[Option[User]](None).
    renderBackend[Backend].
    componentDidMount(_.backend.loadUser).
    build
}
