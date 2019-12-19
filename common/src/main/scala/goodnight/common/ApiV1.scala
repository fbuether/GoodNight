
package goodnight.common


object ApiV1 {
  object Frontend extends ApiPath("GET", C("/"))
  object Asset extends ApiPath("GET", C("/assets/"), R)

  // Xhr Api
  private val p = C("/api/v1/")

  //
  // Authentication
  //
  object SignUp extends ApiPath("POST", p, C("auth"))
  object EmailConfirm extends ApiPath("POST", p, C("auth/confirm"), S)
  object Authenticate extends ApiPath("POST", p, C("auth/authenticate"))
  object SocialAuthenticate extends ApiPath("POST", p,
    C("auth/authenticate/social"), S)
  object SignOut extends ApiPath("POST", p, C("auth/logout"))
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

  // all available (to the user or publicly) stories.
  // may be filtered by header parameters:
  // - "myself" => stories written by the requester
  // returns Ok(Seq[model.Story]) on success.
  object Stories extends ApiPath("GET", p, C("stories"))


  // Story returns a specific story, and, if it exists, the state of the
  // current player in this story.
  // type:
  // (model.Story, // this story
  //  Option[(model.Player, // the player of the current user, if any
  //          model.Activity, // the player's last activity
  //          model.Scene)]) // the scene of the player's last activity
  object Story extends ApiPath("GET", p, C("story/"), S)


  // CreatePlayer returns the new player along with the first activity
  // item and its scene, similar to Story.
  // type: (model.Player, model.Activity, model.Scene)
  object CreatePlayer extends ApiPath("POST", p,
    C("story/"), S, C("/new-player"))


  // load all info about a scene.
  // returns the scene model, as well as a list of next scenes.
  // type: (model.Scene, Seq[model.Scene])
  object Scene extends ApiPath("GET", p,
    C("story/"), S, C("/scene/"), S)


  //
  // Perform Player Activity:

  // Return 202 (Accepted) on success, and 409 (Conflict) if anything is wrong.
  // Requires the the sceneUrlname as parameter.
  // on success, returns the outcome as well as the new scene and new options.
  // type: (model.Activity, model.Scene, Seq[model.Scene])
  object Do extends ApiPath("POST", p, C("story/"), S, C("/do/"), S)


  //
  // Editing Stories
  //
  object CreateStory extends ApiPath("POST", p, C("createStory"))
  object CreateScene extends ApiPath("POST", p, C("story/"), S, C("/scenes"))
  object EditScene extends ApiPath("POST", p, C("story/"), S, C("/scene/"), S)
  object Scenes extends ApiPath("GET", p, C("story/"), S, C("/scenes"))
}
