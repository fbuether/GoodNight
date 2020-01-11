
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
  // returns Ok(Seq[model.play.Story]) on success.
  object Stories extends ApiPath("GET", p, C("stories"))


  // type: PlayerActivity = model.Player, model.Activity

  // Story returns a specific story, and, if it exists, the state of the
  // current player in this story.
  // type:
  // read.StoryState = (Story, Option[PlayerState])
  object Story extends ApiPath("GET", p, C("story/"), S)




  //
  // Perform Player Activity:


  // CreatePlayer returns the new player along with the first activity
  // item and its scene, similar to Story.
  // type: (PlayerActivity, model.SceneView)
  // compare ApiV1.Story() reply type.
  object CreatePlayer extends ApiPath("POST", p,
    C("story/"), S, C("/new-player"))

  object CreateTemporary extends ApiPath("POST", p,
    C("story/"), S, C("/new-temporary-player"))


  // Return 202 (Accepted) on success, and 409 (Conflict) if anything is wrong.
  // Requires the the storyUrlname and the sceneUrlname as parameter.
  // on success, returns the outcome as well as the new scene.
  // todo: return results of activity and changes of state.
  // type: (model.Activity, model.SceneView)
  object DoScene extends ApiPath("POST", p, C("story/"), S, C("/do/"), S)



  //
  // Editing Stories
  //

  // creates a new story.
  // expects the new name as the body.
  // returns 201 (Created) on success with the new story as body.
  // returns 409 (Conflict) on error.
  object CreateStory extends ApiPath("POST", p, C("createStory"))

  // load all scenes as well as qualities of a story.
  // type: (Seq[model.Scene], Seq[model.Quality])
  object Content extends ApiPath("GET", p, C("story/"), S, C("/content"))

  // load all info about a scene.
  // type: model.Scene
  object Scene extends ApiPath("GET", p, C("story/"), S, C("/scene/"), S)

  // create or update a story.
  // both actions require a body with the scene text.
  // replies 202 (Accepted) on success, and 409 (Conflict) if anything is
  // wrong.
  object CreateScene extends ApiPath("POST", p, C("story/"), S, C("/scenes"))
  object SaveScene extends ApiPath("POST", p, C("story/"), S, C("/scene/"), S)

  // reading and changing qualities.
  object Quality extends ApiPath("GET", p, C("story/"), S, C("/quality/"), S)
  object CreateQuality extends ApiPath("POST", p,
    C("story/"), S, C("/qualitys"))
  object SaveQuality extends ApiPath("POST", p, C("story/"), S, C("/quality/"), S)

}
