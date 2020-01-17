
package goodnight.server

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.SilhouetteProvider
import com.mohiva.play.silhouette.api.actions.DefaultSecuredAction
import com.mohiva.play.silhouette.api.actions.DefaultSecuredRequestHandler
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredAction
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredRequestHandler
import com.mohiva.play.silhouette.api.actions.DefaultUserAwareAction
import com.mohiva.play.silhouette.api.actions.DefaultUserAwareRequestHandler
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.authenticators.BearerTokenAuthenticator
import com.mohiva.play.silhouette.impl.authenticators.BearerTokenAuthenticatorService
import com.mohiva.play.silhouette.impl.authenticators.BearerTokenAuthenticatorSettings
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.PlayCacheLayer
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.CacheAuthenticatorRepository
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import controllers.AssetsComponents
import play.api.Application
import play.api.ApplicationLoader
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.LoggerConfigurator
import play.api.Mode
import play.api.NoHttpFiltersComponents
import play.api.cache.caffeine.CaffeineCacheComponents
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.DbName
import play.api.db.slick.SlickComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.mvc.BodyParsers
import play.api.mvc.DefaultActionBuilder
import play.api.mvc.EssentialFilter
import play.api.mvc.PlayBodyParsers
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFFilter
import play.filters.gzip.GzipFilter
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import scala.concurrent.Future
import play.api.mvc.Result
import play.api.mvc.RequestHeader
import play.api.mvc.ControllerHelpers

import goodnight.api.Profile
import goodnight.api.authentication.AuthEnvironment
import goodnight.api.authentication.CredentialAuthInfoRepository
import goodnight.api.authentication.DatabaseAuthenticatorRepository
import goodnight.api.authentication.SignIn
import goodnight.api.authentication.SignUp
import goodnight.api.authentication.UserService
import goodnight.client.Frontend
import goodnight.stories.read
import goodnight.stories.write


class ServerLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach({
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    })
    new GoodnightComponents(context).application
  }
}


class GoodnightComponents(context: Context)
    extends GoodnightRawComponents(context)
    with HttpFiltersComponents {
  override def httpFilters: Seq[EssentialFilter] = {
    super.httpFilters.
      filterNot(_.getClass == classOf[CSRFFilter]).
      :+(new GzipFilter())
  }
}


class GoodnightNoHttpFiltersComponents(context: Context)
    extends GoodnightRawComponents(context)
    with NoHttpFiltersComponents


abstract class GoodnightRawComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with SlickComponents
    with SlickEvolutionsComponents
    with EvolutionsComponents
    with AssetsComponents
    with CaffeineCacheComponents {

  //
  // database

  lazy val database = slickApi.dbConfig[PostgresProfile](DbName("goodnight")).db

  // run the database evolution scripts
  applicationEvolutions

  //
  // play components

  lazy val bodyParsers = PlayBodyParsers()
  lazy val defaultBodyParsers = new BodyParsers.Default(bodyParsers)

  lazy val actionBuilder = DefaultActionBuilder(bodyParsers.defaultBodyParser)

  //
  // silhouette for authentication

  lazy val authInfoRepository = new DelegableAuthInfoRepository(
    new CredentialAuthInfoRepository(database))

  lazy val passwordHasherRegistry = new PasswordHasherRegistry(
      new BCryptSha256PasswordHasher())
  lazy val credentialsProvider = new CredentialsProvider(
    authInfoRepository,
    passwordHasherRegistry)

  lazy val silhouette = new SilhouetteProvider(
    Environment[AuthEnvironment](
      new UserService(database),
      new BearerTokenAuthenticatorService(
        settings = new BearerTokenAuthenticatorSettings(
          authenticatorExpiry = FiniteDuration(30, duration.DAYS)
        ),
        repository = new DatabaseAuthenticatorRepository(database),
        idGenerator = new SecureRandomIDGenerator(),
        clock = Clock()),
      Seq(),
      EventBus()),
    new DefaultSecuredAction(
      new DefaultSecuredRequestHandler(
        new SecuredErrorHandler {
          def onNotAuthenticated(implicit request: RequestHeader):
              Future[Result] =
            Future.successful(ControllerHelpers.Unauthorized)
          def onNotAuthorized(implicit request: RequestHeader):
              Future[Result] =
            Future.successful(ControllerHelpers.Forbidden)
        }),
      defaultBodyParsers),
    new DefaultUnsecuredAction(
      new DefaultUnsecuredRequestHandler(
        new UnsecuredErrorHandler {
          def onNotAuthorized(implicit request: RequestHeader):
              Future[Result] =
            Future.successful(ControllerHelpers.Forbidden)
        }),
      defaultBodyParsers),
    new DefaultUserAwareAction(
      new DefaultUserAwareRequestHandler(),
      defaultBodyParsers))

  //
  // controllers.

  lazy val cc = controllerComponents

  lazy val frontend = new Frontend(cc, assetsFinder, context.environment.mode)

  lazy val authSignUp = new SignUp(cc, database, silhouette,
    passwordHasherRegistry, authInfoRepository)
  lazy val authSignIn = new SignIn(cc, database, silhouette,
    credentialsProvider)
  lazy val profile = new Profile(cc, database, silhouette)

  lazy val readStories = new read.Story(cc, database, readPlayer, silhouette)
  lazy val readScene = new read.Scene(cc, database, readPlayer, silhouette)
  lazy val readChoices = new read.Choices(cc, database, silhouette)
  lazy val readPlayer = new read.Player(cc, database, silhouette, authSignUp)
  lazy val writeStory = new write.Story(cc, database, silhouette)
  lazy val writeScene = new write.Scene(cc, database, silhouette)

  //
  // router, combines routes with controllers.

  lazy val router = new Router(actionBuilder, bodyParsers, frontend,
    authSignUp, authSignIn, profile, readStories, readScene, readChoices,
    readPlayer, writeStory, writeScene, assets)
}
