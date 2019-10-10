
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner


object About {
  val component = ScalaComponent.builder[pages.Router]("About").
    render_P(router =>
      <.div(
        <.h2("What we do"),
        <.p("""We are a small team of developers from northern Germany, near
          the lovely baltic sea. We spend our spare time to further the
          GoodNight, write stories for it, or chat on the boards."""),
        <.p("""You can get in contact with us on the various community areas
          (see the community), or by mail via """,
          <.a(^.href := "mailto:goodnight@jasminefields.net",
            "goodnight@jasminefields.net"),
           """. As we do have daytime jobs, please understand that we might
           not reply right away. We however do try to answer every mail as
           fast as possible."""),
        <.h3("Closing Thoughts"),
        <.p("""Sadly we do not have a team photo yet, so here is an image
          of Nyan Cat: """),
        <.div(
          <.img(^.className := "nyan centered",
            ^.src := (router.baseUrl +
              "assets/images/nyannyannyannyan.gif").value)))).
    build


  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router,
        "By his conception of time, his life will last for over " +
          "2000 years..png", "About the Team"),
      this.component(router))
}
