
package goodnight.common


object ApiV1 {
  object Frontend extends ApiPath("GET", C("/"))
  object Asset extends ApiPath("GET", C("/assets/"), R)

  // Xhr Api
  private val p = C("/api/v1/")

  object SignUp extends ApiPath("PUT", p, C("auth"))
  object EmailConfirm extends ApiPath("POST", p, C("auth/confirm"), S)
  object Authenticate extends ApiPath("POST", p, C("auth/authenticate"))
  object SocialAuthenticate
      extends ApiPath("POST", p, C("auth/authenticate/social"), S)
  object SignOut extends ApiPath("DELETE", p, C("auth"))
  object RequestPasswordReset extends ApiPath("POST", p, C("auth/reset"))
  object ConfirmPasswordReset extends ApiPath("POST", p, C("auth/reset"), S)

  object Stories extends ApiPath("GET", p, C("stories"))
  object Story extends ApiPath("GET", p, C("story/"), S)
  object Scenes extends ApiPath("GET", p, C("story/"), S, C("/scenes"))
  object CreateStory extends ApiPath("PUT", p, C("createStory"))
  object Scene extends ApiPath("GET", p, C("story/"), S, C("/scene/"), S)

  object CreateScene extends ApiPath("PUT", p, C("story/"), S, C("/scenes"))
  object EditScene extends ApiPath("POST", p, C("story/"), S, C("/scene/"), S)

  object Profile extends ApiPath("GET", p, C("profile/"), S)
  object Self extends ApiPath("GET", p, C("self"))
}
