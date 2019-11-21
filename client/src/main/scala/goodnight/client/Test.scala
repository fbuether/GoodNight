
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.version.BuildInfo



object Test {

  val component = ScalaComponent.builder.static("Test")(

    <.div(^.id := "matter",
      <.div(^.id := "side",
        <.h4("Sir Archibald"),
        <.ul(^.className := "quality small",
          <.li(
            <.a(^.href := "#",
              <.img(^.className := "small inline",
                ^.src := "assets/images/buuf/" +
                  "Plasma TV.png"),
              <.span("Rohe Kraft")),
            <.span(^.className := "level",
              "(27)")),
          <.li(
            <.a(^.href := "#",
              <.img(^.className := "small inline",
                ^.src := "assets/images/buuf/" +
                  "Tree.png"),
              <.span("Vitalität")),
            <.span(^.className := "level",
              "(22)")),
          <.li(
            <.a(^.href := "#",
              <.img(^.className := "small inline",
                ^.src := "assets/images/buuf/" +
                  "Spit, eject.png"),
              <.span("Hammer")))

      )),
      <.div(^.className := "centre",

        <.h2("An den Toren des Schlosses"),

        <.p("""Grau und leer erhebt sich das alte Schloss direkt vor dir.
Die schmalen, hohen Fenster sind leer, und das eingefallene Dach zeugt von langer Zeit der Vernachlässigung. Hier ist niemand zuhause, der stolz darauf ist.
"""),

        <.p(^.className := "call",
          "Was möchtest du hier tun?"),

        <.ul(^.className := "choices as-items links",
          <.li(
            <.a(^.href := "#",
              <.img(^.className := "left",
                ^.src := ("assets/images/buuf/" +
                  "I can help you my son, I am Paddle Paul..png"
                )),
              <.ul(^.className := "requirements",
                <.li(^.className := "tooltip-anchor",
                  <.img(^.src := "assets/images/buuf/" + "Plasma TV.png"),
                  <.span(^.className := "tooltip",
                    <.strong("Rohe Kraft"),
                    <.span("benötigt: 20"),
                    <.span("du hast: 27"))),
                <.li(^.className := "tooltip-anchor",
                  <.img(^.src := "assets/images/buuf/" + "Chea.png"),
                  <.span(^.className := "tooltip",
                    <.strong("Hammer"),
                    <.span("benötigt: vorhanden"),
                    <.span("du hast: vorhanden")))),
              <.h4("Versuche, das Tor zu öffnen"),
              <.p("Auch wenn die Eichenbalken des Tores sehr solide aussehen, sind sie ja vielleicht genauso verfallen wie das Schloss selbst.")
            )),
          <.li(^.className := "disabled",
              <.img(^.className := "left",
                ^.src := ("assets/images/buuf/" +
                  "Tree.png"
                )),
              <.ul(^.className := "requirements",
                <.li(^.className := "tooltip-anchor",
                  <.img(^.src := "assets/images/buuf/" + "Bomb.png"),
                  <.span(^.className := "tooltip",
                    <.strong("Rohe Kraft"),
                    <.span("benötigt: 20"),
                    <.span("du hast: 27"))),
                <.li(^.className := "tooltip-anchor disabled",
                  <.img(^.src := "assets/images/buuf/" + "Blue Soap.png"),
                  <.span(^.className := "tooltip",
                    <.strong("Hammer"),
                    <.span("benötigt: vorhanden"),
                    <.span("du hast: nichts")))),
              <.h4("Klettere die Wand hinauf"),
              <.p("Der Efeu an der Wand sieht sehr festgewachsen aus. Dort Hinaufzuklettern ist bestimmt unproblematisch.")
            )

        )
        ))
    ).
      build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router,
        "Newer Looking, But Older Rocket.png", "Testpage"),
      this.component())
}
