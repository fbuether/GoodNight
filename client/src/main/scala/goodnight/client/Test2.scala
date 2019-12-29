
package goodnight.home

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.client.pages
import goodnight.components.Shell
import goodnight.components.Banner
import goodnight.version.BuildInfo



object Test2 {

  val component = ScalaComponent.builder.static("Test")(

      <.div(^.className := "overlay-anchor",


        <.div(^.className := "edit-canvas",

          <.div(^.className := "scene",
            <.div(
              <.i(^.className := "fas fa-scroll"),
              <.span("At the docks, there is but not a sound to hear"),
              <.a(^.href := "#test2/at-the-docks",
                ^.title := "Edit this scene",
                <.i(^.className := "fas fa-pen-fancy")),
              <.a(^.className := "clickable",
                ^.href := "#",
                ^.title := "Copy this scene",
                <.i(^.className := "far fa-copy")),
              <.a(^.className := "clickable danger",
                ^.href := "#",
                ^.title := "Delete this scene",
                <.i(^.className := "far fa-trash-alt"))),
            <.p("""There is a certain atmosphere at the docks that seems to claw
                at your nose like a hungry sewer rat. People bustle about""")// ,
            // <.div(^.className := "tags",
            //   <.span("docks"),
            //   <.span("intro"),
            //   <.span("conflict"))
          ),

          <.div(^.className := "quality",
            <.div(
              <.i(^.className := "fas fa-hammer"),
              <.span("Cold, hard cash"),
              <.a(^.href := "#test2/at-the-docks",
                ^.title := "Edit this quality"),
              <.i(^.className := "fas fa-pen-fancy"),
              <.a(^.className := "clickable",
                ^.href := "#",
                ^.title := "Copy this quality",
                <.i(^.className := "far fa-copy")),
              <.a(^.className := "clickable danger",
                ^.href := "#",
                ^.title := "Delete this quality",
                <.i(^.className := "far fa-trash-alt"))),
            <.p("""You gotta pay for what you take. That's how
                it's always been."""),
            <.div(^.className := "tags"))),

        <.p(
        <.button(
          // props.router.setOnClick(pages.AddScene(props.story.urlname)),
          <.i(^.className := "fas fa-plus-circle"),
          "New Scene")))




//     <.div(^.id := "matter",
//       <.div(^.className := "centre",

//         <.h2("An den Toren des Schlosses"),

//         <.p("""Grau und leer erhebt sich das alte Schloss direkt vor dir.
// Die schmalen, hohen Fenster sind leer, und das eingefallene Dach zeugt von langer Zeit der Vernachlässigung. Hier ist niemand zuhause, der stolz darauf ist.
// """),

//         <.p(^.className := "call",
//           "Was möchtest du hier tun?"),

//         <.ul(^.className := "choices as-items",
//           <.li(
//             <.img(^.className := "left",
//               ^.src := ("assets/images/buuf/" +
//                 "I can help you my son, I am Paddle Paul..png"
//               )),
//             <.ul(^.className := "requirements as-icons",
//               <.li(^.className := "tooltip-anchor",
//                 <.img(^.src := "assets/images/buuf/" + "Plasma TV.png"),
//                 <.div(^.className := "tooltip",
//                   <.strong("Rohe Kraft"),
//                   <.span("benötigt: 20"),
//                   <.span("du hast: 27"))),
//               <.li(^.className := "tooltip-anchor",
//                 <.img(^.src := "assets/images/buuf/" + "Chea.png"),
//                 <.div(^.className := "tooltip",
//                   <.strong("Hammer"),
//                   <.span("benötigt: vorhanden"),
//                   <.span("du hast: vorhanden")))),
//             <.h4("Versuche, das Tor zu öffnen"),
//             <.p("Auch wenn die Eichenbalken des Tores sehr solide aussehen, sind sie ja vielleicht genauso verfallen wie das Schloss selbst.",
//             <.button(^.className := "right",
//               <.span(^.className := "fas fa-angle-double-right")))
//           ),

//         <.li(^.className := "disabled",
//           <.img(^.className := "left",
//             ^.src := ("assets/images/buuf/" +
//               "Tree.png"
//             )),
//           <.ul(^.className := "requirements as-icons",
//             <.li(^.className := "tooltip-anchor",
//               <.img(^.src := "assets/images/buuf/" + "Bomb.png"),
//               <.span(^.className := "tooltip",
//                 <.strong("Rohe Kraft"),
//                 <.span("benötigt: 20"),
//                 <.span("du hast: 27"))),
//             <.li(^.className := "tooltip-anchor disabled",
//               <.img(^.src := "assets/images/buuf/" + "Blue Soap.png"),
//               <.span(^.className := "tooltip",
//                 <.strong("Hammer"),
//                 <.span("benötigt: vorhanden"),
//                 <.span("du hast: nichts")))),
//           <.h4("Klettere die Wand hinauf"),
//           <.p("Der Efeu an der Wand sieht sehr festgewachsen aus. Dort Hinaufzuklettern ist bestimmt unproblematisch.")
//         )

//         )
//         ),
//       <.div(^.id := "side",
//         <.h4("Sir Archibald"),
//         <.ul(^.className := "quality small",
//           <.li(
//             <.a(^.href := "#",
//               <.img(^.className := "small inline",
//                 ^.src := "assets/images/buuf/" +
//                   "Plasma TV.png"),
//               <.span("Rohe Kraft")),
//             <.span(^.className := "level",
//               "(27)")),
//           <.li(
//             <.a(^.href := "#",
//               <.img(^.className := "small inline",
//                 ^.src := "assets/images/buuf/" +
//                   "Tree.png"),
//               <.span("Vitalität")),
//             <.span(^.className := "level",
//               "(22)")),
//           <.li(
//             <.a(^.href := "#",
//               <.img(^.className := "small inline",
//                 ^.src := "assets/images/buuf/" +
//                   "Spit, eject.png"),
//               <.span("Hammer")))

//       )))
    ).
      build

  def render(router: pages.Router) =
    Shell.component(router)(
      Banner.component(router,
        "Newer Looking, But Older Rocket.png", "Edit Story: Der letzte Beutezug"),
      this.component())
}
