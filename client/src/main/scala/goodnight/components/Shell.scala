
package goodnight.components

import java.time.Year

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages


object Shell {
  val footer = ScalaComponent.builder[(Int,RouterCtl[pages.Page])]("Footer").
    render_P({ case (year, router) =>
      <.div(^.className := "schlussvermerk",
        "© " + year + " ", // Year.now.getValue
        <.a(^.href := "https://jasminefields.net",
          ^.title := "jasminéfields.net",
          ^.target := "_blank",
          "jasminéfields.net"),
        " ~ ",
        // about
        // mailto
        <.img(^.src := (router.baseUrl + "assets/images/othftwy.gif").value,
          ^.title := "on the hunt for the white yonder")) }).
    componentWillMount(u => Callback(println("mount.footer"))).
    componentWillUpdate(u => Callback(println("update.footer"))).
    componentWillUnmount(u => Callback(println("unmount.footer"))).
    build


  type Props = (RouterCtl[pages.Page])

  val component = ScalaComponent.builder[Props]("Shell").
    render_PC((router, children) =>
      <.div(^.className := "goodnight",
        Menu.component(router),
        children,
        footer(Year.now.getValue, router))).
    componentWillMount(u => Callback(println("mount.shell"))).
    componentWillUpdate(u => Callback(println("update.shell"))).
    componentWillUnmount(u => Callback(println("unmount.shell"))).
    build
}

