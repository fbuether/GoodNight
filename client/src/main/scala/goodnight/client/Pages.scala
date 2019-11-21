
package goodnight.client

import japgolly.scalajs.react.extra.router.RouterCtl



package pages {
  sealed trait Page

  case object Home extends Page
  case object Community extends Page
  case object Profile extends Page
  case object About extends Page

  case object Test extends Page

  // authentication
  case object Register extends Page
  case object SignIn extends Page
  case class SignInFor(page: String) extends Page
  case object RequestPasswordReset extends Page

  // Reading Stories
  case object Stories extends Page
  case class Story(story: String) extends Page
  case class Inventory(story: String) extends Page
  case class Scene(story: String) extends Page

  // Creating stories
  case object CreateStory extends Page
  case class EditStory(story: String) extends Page

  case class AddScene(story: String) extends Page
  case class EditScene(story: String, scene: String) extends Page
  // case class CopyScene(story: String, scene: String) extends Page
  // case class DeleteScene(story: String, scene: String) extends Page

  case class AddQuality(story: String) extends Page
  case class EditQuality(story: String, quality: String) extends Page

  case class AddLocation(story: String) extends Page
}

package object pages {
  type Router = RouterCtl[Page]
}
