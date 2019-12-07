
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

  // Story returns a specific story, and, if it exists, the state of the
  // current player in this story (player + last playeraction)
  // type:
  // [(model.Story, // this story
  //   Option[model.Player])] // the player of the current user, if any
  object Story extends ApiPath("GET", p, C("story/"), S)
  object CreatePlayer extends ApiPath("PUT", p,
    C("story/"), S, C("/new-player"))
  // availableScenes shows all Scenes without a location; if all scenes
  // have a location, always use AvailableScenesAt.
  object AvailableScenes extends ApiPath("GET", p, C("story/"), S, C("/scenes"))
  object AvailableScenesAt extends ApiPath("GET", p, C("story/"), S,
    C("/scenes/at/"), S)
  object AvailableChoices extends ApiPath("GET", p,
    C("story/"), S, C("/scene/"), S)

  //
  // Perform Player Activity:
  //
  // All these actions return 202 (Accepted) on success, and 409 (Conflict)
  // if anything is wrong. Content describes the outcome to the player, if
  // any major change happens. Continuations can be fetched with the
  // Available* Actions.

  // enter a scene.
  // This is only allowed if the player is in state
  // State.Location(story, scene.location), and meets the requirements of the
  // scene.
  // Returns a Seq[model.Effect?] caused by entering this scene, if any.
  // player state afterwards is State.Scene(story, scene)
  object DoScene extends ApiPath("POST", p,
    C("story/"), S, C("/go/scene/"), S)

  // Perform a choice.
  // This is only allowed if the player is in state State.Scene(story, scene),
  // and meets the requirements for this choice.
  // Returns a Seq[model.Effect?] caused by performing this scene, if any.
  // player state afterwards is State.Choice(story, scene, choice)
  object DoChoice extends ApiPath("POST", p,
    C("story/"), S, C("/go/scene/"), S, C("/choose/"), S)

  // move to a different location.
  // This is only allowede if the player is in state State.Location(story, _),
  // and meets the location's requirements.
  // Returns a Seq[model.Effect?] caused by moving to the new location, if any.
  // player state afterwards is State.Location(story, location).
  object DoLocationNone extends ApiPath("POST", p,
    C("story/"), S, C("/go-nowhere"))
  object DoLocation extends ApiPath("POST", p,
    C("story/"), S, C("/go-to/"), S)

  //
  // Editing Stories
  //
  object CreateStory extends ApiPath("PUT", p, C("createStory"))
  object CreateScene extends ApiPath("PUT", p, C("story/"), S, C("/scenes"))
  object EditScene extends ApiPath("POST", p, C("story/"), S, C("/scene/"), S)
  object Scenes extends ApiPath("GET", p, C("story/"), S, C("/scenes"))
}
