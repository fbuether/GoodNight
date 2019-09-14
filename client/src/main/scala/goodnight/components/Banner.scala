
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages


object Banner {
  type Props = (RouterCtl[pages.Page], String, String)

  val component = ScalaComponent.builder[Props]("Banner").
    render_P({ case (router, icon, title) =>
      <.h1(^.className := "banner",
        <.img(^.src := (router.baseUrl + "assets/images/buuf/" + icon).value),
        <.span(title)) }).
    componentWillMount(u => Callback(println("mount.banner"))).
    componentWillUpdate(u => Callback(println("update.banner"))).
    componentWillUnmount(u => Callback(println("unmount.banner"))).
    build
}
