
package goodnight.components

import org.scalajs.dom.html

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl

import goodnight.client.Pages


object Menu {
  case class Props(
    router: RouterCtl[Pages.Page]
  )

  type State = (Boolean)

  class Backend(bs: BackendScope[Props, State]) {
    val menuRef = Ref[html.Div]

    def expandMenu: Callback =
      menuRef.foreach({ menu =>
        val cn = menu.className
        if (cn.endsWith("expanded"))
          menu.className = cn.substring(0, cn.indexOf("expanded") - 1)
        else
          menu.className = cn + " expanded"
      })

    def render(p: Props): VdomElement =
      <.div(^.className := "menu",
        <.ul(
          <.li(^.className := "header",
            p.router.link(Pages.Home)(
              <.span(^.className := "fa fa-moon-o"),
              " GoodNight")),
          <.li(
            p.router.link(Pages.Worlds)(
              <.span(^.className := "fa fa-globe"),
              " Worlds")),
          <.li(
            p.router.link(Pages.Community)(
              <.span(^.className := "fa fa-comment-o"),
              " Community"))),
        <.ul(
          <.li(^.className := "expander",
            <.a(^.onClick --> expandMenu,
              <.span(^.className := "fa fa-navicon"),
              " Menu")),
          <.li(
            p.router.link(Pages.Register)(
              <.span(^.className := "fa fa-bookmark-o"),
              " Register")),
          <.li(
            p.router.link(Pages.SignIn)(
              <.span(^.className := "fa fa-check-square-o"),
              " Sign in")))).withRef(menuRef)
  }

  def component = ScalaComponent.builder[Props]("Menu").
    initialState(false).
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
