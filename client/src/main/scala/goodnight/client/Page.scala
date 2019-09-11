
package goodnight.client

import scala.language.higherKinds

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.extra.router.StaticDsl._
import japgolly.scalajs.react.component.Scala._

trait Page

trait PageDescriptor {
  type Dsl = RouterConfigDsl[Page]
  type Route = Rule[Page]

  def getRoute(dsl: Dsl): Route
}

trait StaticPageDescriptor extends PageDescriptor {
  protected def route(dsl: RouterConfigDsl[Page]): Path
  protected def showPage(dsl: RouterConfigDsl[Page]): Renderer[Page]
  protected def getPage: Page

  override def getRoute(dsl: Dsl): Route = {
    import dsl._
    staticRoute(route(dsl), getPage) ~> showPage(dsl)
  }
}

//  {

// // }

// trait StaticPage extends Page {
//   def getRoute(dsl: Dsl): Rule[Page] = {
//     import dsl._
//     staticRoute(route(dsl), this) ~> showPage(dsl)
//   }

// }

// //   trait StaticPage extends Page {
// //     def getRoute(dsl: RouterConfigDsl[Page]) = {
// //       import dsl._
// //       staticRoute(route, this)
// //     }

// //     def route: String
// //   }

// //   case object Home extends StaticPage {
// //     def route = "/"
// //   }

// //   case object Worlds extends StaticPage {
// //     def route = "/#/worlds"
// //   }

// //   case object Community extends StaticPage {
// //     def route = "/#/community"
// //   }

// //   case object Register extends StaticPage {
// //     def route = "/#/auth/register"
// //   }

// //   case object SignIn extends StaticPage {
// //     def route = "/#/auth/sign-in"
// //   }

// //   case object RequestPasswordReset extends StaticPage {
// //     def route = "/#/auth/request-password-reset"
// //   }
// // // }
