
package goodnight.client

import org.scalajs.dom.document
import scala.scalajs.js.annotation._
import scala.scalajs.js

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
// import japgolly.scalajs.react.vdom.html_<^._

import goodnight.home._
import goodnight.home.authentication._
import goodnight.stories._
import goodnight.stories.edit
import goodnight.community._

import goodnight.service.AuthenticationService


object Client {

  val routes = RouterConfigDsl[pages.Page].buildConfig({ dsl =>
    import dsl._

    val anyName = string("[^/]+")

    // pages that can be accessed even when not logged in.
    val publicPages = (
      staticRoute(root, pages.Home) ~> renderR(Home.render) |
      staticRoute("#community", pages.Community) ~> renderR(Community.render) |
      staticRoute("#about", pages.About) ~> renderR(About.render) |
      staticRoute("#test", pages.Test) ~> renderR(Test.render) |
      staticRoute("#test2", pages.Test2) ~> renderR(Test2.render) |

      //
      // reading stories, as they may be public.
      {val route = ("#story" / anyName / "continue").caseClass[pages.Story]
        dynamicRouteCT(route) ~> dynRenderR(Story.render)}
    )

    // pages that require no valid authentication (as opposed to any state).
    val unauthenticatedPages = (
      //
      // Authentication
      //
      {staticRoute("#auth/register", pages.Register) ~>
        renderR(Register.render)} |
      {staticRoute("#auth/sign-in", pages.SignIn) ~>
        renderR(SignIn.render)} |
      {dynamicRouteCT(("#auth/sign-in/to" / string(".+")).
        caseClass[pages.SignInFor]) ~> dynRenderR(SignIn.renderFor)} |
      (staticRoute("#auth/reset-password", pages.RequestPasswordReset) ~>
        renderR(PasswordReset.render))
    )

    // pages that require a valid authentication.
    val authenticatedPages = (
      //
      // Profile
      //
      {staticRoute("#profile", pages.Profile) ~> renderR(Profile.render)} |

      //
      // Reading Stories
      //
      staticRoute("#stories", pages.Stories) ~> renderR(Stories.render) |

      //
      // Editing Stories
      //
      {staticRoute("#write/new-story", pages.CreateStory) ~>
        renderR(edit.CreateStory.render)} |
      {dynamicRouteCT(("#write/story" / anyName).
        caseClass[pages.EditStory]) ~> dynRenderR(edit.Story.render)} |
      //
      // Editing Scenes
      {dynamicRouteCT(("#write/story" / anyName / "new-scene").
        caseClass[pages.AddScene]) ~> dynRenderR(edit.SceneWrap.renderAdd)} |
      {dynamicRouteCT(("#write/story" / anyName / "new-scene" / anyName).
        caseClass[pages.AddSceneNamed]) ~>
        dynRenderR(edit.SceneWrap.renderAddNamed)} |
      {dynamicRouteCT(("#write/story" / anyName / "scene" / anyName).
        caseClass[pages.EditScene]) ~> dynRenderR(edit.SceneWrap.renderEdit)} |
      //
      // Editing Qualities
      {dynamicRouteCT(("#write/story" / anyName / "new-quality").
        caseClass[pages.AddQuality]) ~> dynRenderR(edit.Quality.renderAdd)} |
      {dynamicRouteCT(("#write/story" / anyName / "quality" / anyName).
        caseClass[pages.EditQuality]) ~> dynRenderR(edit.Quality.renderEdit)}
    )


    def requireSignIn(rules: Rules)(page: pages.Page) =
      redirectToPage(pages.SignInFor(rules.path(page).value))(Redirect.Push)

    // requires recursive definition in order to unconvert pages in
    // sign-in-target dialogue.
    lazy val finalRoute: Rules = (trimSlashes |
      authenticatedPages.
      addCondition(AuthenticationService.isSignedIn)(
        requireSignIn(finalRoute)) |
      unauthenticatedPages.
      addCondition(AuthenticationService.isSignedIn.map(!_))(
        _ => redirectToPage(pages.Profile)(Redirect.Push)) |
      publicPages)

    finalRoute.
      notFound(redirectToPage(pages.Home)(Redirect.Push)).
      onPostRender((_, next) => Callback({
        JSGlobal.trackPageview(finalRoute.path(next).value)
      }))
  })

  @js.native
  @JSGlobalScope
  private object JSGlobal extends js.Object {
    def trackPageview(nextPath: String): Unit = js.native
  }

  def main(args: Array[String]): Unit = {
    val router = Router(BaseUrl.fromWindowOrigin_/, routes)
    router().renderIntoDOM(document.getElementById("goodnight-client"))
  }
}
