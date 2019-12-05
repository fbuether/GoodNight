
package goodnight.common


object ApiV1 {
  object Frontend extends ApiPath("GET", C("/"))
  object Asset extends ApiPath("GET", C("/assets/"), R)

  // Xhr Api
  private val p = C("/api/v1/")

  //
  // Authentication
  //
  object SignUp extends ApiPath("PUT", p, C("auth"))
  object EmailConfirm extends ApiPath("POST", p, C("auth/confirm"), S)
  object Authenticate extends ApiPath("POST", p, C("auth/authenticate"))
  object SocialAuthenticate extends ApiPath("POST", p,
    C("auth/authenticate/social"), S)
  object SignOut extends ApiPath("DELETE", p, C("auth"))
  object RequestPasswordReset extends ApiPath("POST", p, C("auth/reset"))
  object ConfirmPasswordReset extends ApiPath("POST", p, C("auth/reset"), S)
  object Self extends ApiPath("GET", p, C("self"))

  //
  // Profile
  //
  object Profile extends ApiPath("GET", p, C("profile/"), S)

  //
  // Reading Stories
  //
  object Stories extends ApiPath("GET", p, C("stories"))
  object Story extends ApiPath("GET", p, C("story/"), S)
  object CreatePlayer extends ApiPath("PUT", p,
    C("story/"), S, C("/new-player"))
  // availableScenes shows all Scenes without a location; if all scenes
  // have a location, always use AvailableScenesAt.
  object AvailableScenes extends ApiPath("GET", p, C("story/"), S, C("/scenes"))
  object AvailableScenesAt extends ApiPath("GET", p, C("story/"), S,
    C("/scenes/at/"), S)

  // actual activity
  object DoScene extends ApiPath("POST", p,
    C("story/"), S, C("/go/scene/"), S)
  object DoChoice extends ApiPath("POST", p,
    C("story/"), S, C("/go/scene/"), S, C("/choose/"), S)

  //
  // Editing Stories
  //
  object CreateStory extends ApiPath("PUT", p, C("createStory"))
  object CreateScene extends ApiPath("PUT", p, C("story/"), S, C("/scenes"))
  object EditScene extends ApiPath("POST", p, C("story/"), S, C("/scene/"), S)
  object Scenes extends ApiPath("GET", p, C("story/"), S, C("/scenes"))
}
