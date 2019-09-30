
package goodnight.client


package pages {
  sealed trait Page

  case object Home extends Page
  case object Community extends Page
  case object Profile extends Page
  case object About extends Page

  // authentication
  case object Register extends Page
  case object SignIn extends Page
  case object RequestPasswordReset extends Page

  // playing worlds
  case object Stories extends Page
  case class Story(name: String) extends Page
  case class Inventory(world: String) extends Page
  case class Scene(world: String) extends Page

  // creation
  case object Create extends Page
  case class WriteWorld(name: String) extends Page
  case class WriteStory(world: String, story: String) extends Page
  case class WriteQuality(world: String, quality: String) extends Page
}
