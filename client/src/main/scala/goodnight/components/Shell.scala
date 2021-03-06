
package goodnight.components

import java.time.Year

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.service.AuthenticationService


object Shell {
  val footer = ScalaComponent.builder[pages.Router]("Footer").
    render_P(router =>
      <.div(^.className := "schlussvermerk",
        // "© " + Year.now.getValue + " ",
        <.a(^.href := "https://jasminefields.net",
          ^.title := "jasminéfields.net",
          ^.target := "_blank",
          "jasminéfields.net"),
        " ~ ",
        router.link(pages.About)(
          "the goodnight team"),
        " ~ ",
        <.a(^.href := "mailto:goodnight@jasminefields.net",
          "goodnight@jasminefields.net"),
        " ~ ",
        <.img(^.src := (router.baseUrl + "assets/images/othftwy.gif").value,
          ^.title := "on the hunt for the white yonder"))).
    shouldComponentUpdateConst(false).
    build


  type Props = (pages.Router)

  val component = ScalaComponent.builder[Props]("Shell").
    render_PC((router, children) =>
      <.div(^.className := "goodnight",
        Menu.component(router),
        children,
        footer(router))).
    build
}

