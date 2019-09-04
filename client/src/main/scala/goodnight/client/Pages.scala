
package goodnight.client

import japgolly.scalajs.react.extra.router.StaticDsl.StaticRouteB
import japgolly.scalajs.react.extra.router.StaticDsl.Rule

import japgolly.scalajs.react.extra.router.RouterConfigDsl


object Pages
{
  sealed trait Page {
    def getRoute(dsl: RouterConfigDsl[Page]): StaticRouteB[Page, Rule[Page]]
  }

  trait StaticPage extends Page {
    def getRoute(dsl: RouterConfigDsl[Page]) = {
      import dsl._
      staticRoute("", this)
    }

    def route: String
  }

  case object Home extends StaticPage {
    def route = ""
  }

  case object Worlds extends StaticPage {
    def route = "worlds"
  }

  case object Community extends StaticPage {
    def route = "community"
  }

  case object Register extends StaticPage {
    def route = "register"
  }

  case object SignIn extends StaticPage {
    def route = "sign-in"
  }
}
