
package goodnight.client

import org.scalajs.dom.document

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.home.Home
import goodnight.worlds.World
import goodnight.worlds.Worlds
import goodnight.community.Community
import goodnight.home.authentication.Register
import goodnight.home.authentication.SignIn
import goodnight.home.authentication.PasswordReset


object Client {
  type Dsl = japgolly.scalajs.react.extra.router.RouterConfigDsl[pages.Page]
  val dsl: Dsl = new Dsl()
  import dsl._

  def main(args: Array[String]): Unit = {
    val routerConfig = RouterConfigDsl[pages.Page].buildConfig({ dsl =>
      routes.
        notFound(redirectToPage(pages.Home)(Redirect.Replace)).
        logToConsole
    })
    val router = Router(BaseUrl.fromWindowOrigin, routerConfig)
    val goodnightElement = document.getElementById("goodnight-client")
    router().renderIntoDOM(goodnightElement)
  }

  def routes: Rules = (emptyRule |
    staticRoute(root, pages.Home) ~> renderR(Home.render) |
    staticRoute("/#community", pages.Community) ~> renderR(Community.render) |
    // Authentication.
    staticRoute("/#auth/register", pages.Register) ~> renderR(Register.render) |
    staticRoute("/#auth/sign-in", pages.SignIn) ~> renderR(SignIn.render) |
    (staticRoute("/#auth/reset-password", pages.RequestPasswordReset) ~>
      renderR(PasswordReset.render)) |

    staticRoute("/#worlds", pages.Worlds) ~> renderR(Worlds.render) |
    {val route = ("/#world" / string("[^/]+")).caseClass[pages.World]
      dynamicRouteCT(route) ~> dynRenderR(World.render)}
  )
}


