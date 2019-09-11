
package goodnight.client

import scala.util.{ Either, Left, Right }
import org.scalajs.dom.document

import japgolly.scalajs.react.extra.router._

import goodnight.components.Shell
import goodnight.home.Home
import goodnight.home.NotFound

object Client {
  private def pages: List[PageDescriptor] = List(
    goodnight.home.Home.page,
    goodnight.home.NotFound.page
    // goodnight.home.NotFound.page(new Path("/")),
    // goodnight.community.Community,
    // goodnight.worlds.Worlds,
    // goodnight.home.authentication.Register,
    // goodnight.home.authentication.SignIn,
    // goodnight.home.authentication.PasswordReset
  )

  def main(args: Array[String]): Unit = {
    val routerConfig = RouterConfigDsl[Page].buildConfig(dsl => {
      import dsl._
      this.pages.map(_.getRoute(dsl)).
        foldLeft(emptyRule)(_ | _).
        notFound(path =>
//           // NotFound.component(NotFound.Props(path.value))).
NotFound.NotFoundPage(path.value)).
        // notFound(redirectToPage(Home.HomePage)(Redirect.Replace)).

        logToConsole
    })
    val router = Router(BaseUrl.fromWindowOrigin, routerConfig)
    val root = document.getElementById("goodnight-client")

    router().renderIntoDOM(root)

    // Shell.component(Shell.Props(// router,
    //   "Burn.png", "Title"))(
    //   // router.ctor()//.renderIntoDOM(rootElement)
    //   router()
    // ).renderIntoDOM(rootElement)
  }
}


