
package goodnight.client

import org.scalajs.dom.document

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

import goodnight.home._
import goodnight.home.authentication._
import goodnight.stories._
import goodnight.community._


object Client {

  val routes = RouterConfigDsl[pages.Page].buildConfig({ dsl =>
    import dsl._

    (trimSlashes |
      staticRoute(root, pages.Home) ~> renderR(Home.render) |
      staticRoute("#community", pages.Community) ~> renderR(Community.render) |
      staticRoute("#about", pages.About) ~> renderR(About.render) |
      // Authentication
      {staticRoute("#auth/register", pages.Register) ~>
        renderR(Register.render)} |
      staticRoute("#auth/sign-in", pages.SignIn) ~> renderR(SignIn.render) |
      (staticRoute("#auth/reset-password", pages.RequestPasswordReset) ~>
        renderR(PasswordReset.render)) |
      staticRoute("#profile", pages.Profile) ~> renderR(Profile.render) |
      // Worlds
      {staticRoute("#createStory", pages.CreateStory) ~>
        renderR(CreateStory.render)} |
      staticRoute("#stories", pages.Stories) ~> renderR(Stories.render) |
      {val route = ("#story" / string("[^/]+")).caseClass[pages.Story]
        dynamicRouteCT(route) ~> dynRenderR(Story.render)}).
      notFound(redirectToPage(pages.Home)(Redirect.Replace))
  })

  def main(args: Array[String]): Unit = {
    val router = Router(BaseUrl.fromWindowOrigin_/, routes)
    router().renderIntoDOM(document.getElementById("goodnight-client"))
  }
}


