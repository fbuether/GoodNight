
package goodnight.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.Pages


object Menu {
  case class Props(
    router: RouterCtl[Pages.Page]
  )

  type State = Unit

  class Backend(bs: BackendScope[Props, State]) {
    def render(p: Props): VdomElement =
      <.div(^.className := "menu",
        <.div(
          <.ul(^.className := "left",
            <.li(^.className := "item header",
              p.router.link(Pages.Home)(
                <.span(^.className := "fa fa-moon-o"),
                " GoodNight")),
            <.li(^.className := "item",
              p.router.link(Pages.Worlds)(
                <.span(^.className := "fa fa-globe"),
                " Worlds")),
            <.li(^.className := "item",
              p.router.link(Pages.Community)(
                <.span(^.className := "fa fa-comment-o"),
                " Community"))),
          <.ul(^.className := "right",
            <.li(^.className := "item expander",
              <.a(^.href := "#",
                ^.id := "mainMenuExpander",
                <.span(^.className := "fa fa-navicon"),
                " Menu")),
            <.li(^.className := "item",
              p.router.link(Pages.Register)(
                <.span(^.className := "fa fa-bookmark-o"),
                " Register")),
            <.li(^.className := "item",
              p.router.link(Pages.SignIn)(
                <.span(^.className := "fa fa-check-square-o"),
                " Sign in")))),
        <.div(^.className := "spacer",
          "&nbsp;"))
  }

  def component = ScalaComponent.builder[Props]("Menu").
    stateless.
    renderBackend[Backend].
    build
}


// object Menu
// {
//   type Props = String

//   type State = Unit

//   type Backend = Unit

//   type Context = DuringCallbackU[Props, State, Backend]

//   def apply(props: Props, children: ReactNode*): ReactElement =
//     ReactComponentB[Props]("Menu").
//       render(this.render).
//       build(props, children)

//   def render(me: Context): ReactElement =
//   {
//   }

//   /*


//       <div class="menu">
//         <div>
//           <ul class="left">
//             <li class="item header">
//               <a href="http://goodnight.jasminefields.net/goodnight/">
//                 <span class="fa fa-moon-o"></span> GoodNight
//               </a>
//             </li>
//             <li class="item">
//               <a href="http://goodnight.jasminefields.net/goodnight/worlds">
//                 <span class="fa fa-globe"></span> Worlds
//               </a>
//             </li>
//             <li class="item">
//               <a href="http://goodnight.jasminefields.net/goodnight/social">
//                 <span class="fa fa-comment-o"></span> Community
//               </a>
//             </li>
//           </ul>

//           <ul class="right">
//             <li class="item expander">
//               <a href="#" id="mainMenuExpander">
//                 <span class="fa fa-navicon"></span> Menu
//               </a>
//             </li>
//             <li class="item"><a href="http://goodnight.jasminefields.net/goodnight/user">
//   <span class="fa fa-sun-o"></span> fbu
// </a></li>
// <li class="item">
//   <a href="http://goodnight.jasminefields.net/goodnight/signOut">
//     <span class="fa fa-share-square-o"></span>
//     sign out
//   </a>
// </li>
//           </ul>
//         </div>
//         <div class="spacer">&nbsp;</div>
//       </div>

//    */
// }



    //       <ul class="left">
    //         <li class="item header">
    //           <a href="https://goodnight.jasminefields.net/goodnight/">
    //             <span class="fa fa-moon-o"></span> GoodNight
    //           </a>
    //         </li>
    //         <li class="item">
    //           <a href="https://goodnight.jasminefields.net/goodnight/worlds">
    //             <span class="fa fa-globe"></span> Worlds
    //           </a>
    //         </li>
    //         <li class="item">
    //           <a href="https://goodnight.jasminefields.net/goodnight/social">
    //             <span class="fa fa-comment-o"></span> Community
    //           </a>
    //         </li>
    //       </ul>
