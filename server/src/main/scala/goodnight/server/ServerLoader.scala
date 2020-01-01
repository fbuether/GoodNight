
package goodnight.server

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.SilhouetteProvider
import com.mohiva.play.silhouette.api.actions.DefaultSecuredAction
import com.mohiva.play.silhouette.api.actions.DefaultSecuredErrorHandler
import com.mohiva.play.silhouette.api.actions.DefaultSecuredRequestHandler
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredAction
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredErrorHandler
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

  // run the database evolution scripts
  applicationEvolutions

  lazy val bodyParsers = PlayBodyParsers()
  lazy val actionBuilder = DefaultActionBuilder(bodyParsers.defaultBodyParser)

  lazy val database = slickApi.dbConfig[PostgresProfile](DbName("goodnight")).db


  lazy val userService = new UserService(database)

  lazy val silhouetteEnvironment = Environment[AuthEnvironment](
    userService,
    new BearerTokenAuthenticatorService(
      settings = new BearerTokenAuthenticatorSettings(),
      repository = new DatabaseAuthenticatorRepository(database),
        // new CacheAuthenticatorRepository[BearerTokenAuthenticator](
        //   new PlayCacheLayer(cacheApi("auth-token-cache"))),
      idGenerator = new SecureRandomIDGenerator(),
      clock = Clock()),
    Seq(), // requestProvidersImpl: Seq[RequestProvider]
    EventBus())
  lazy val defaultBodyParsers = new BodyParsers.Default(bodyParsers)

  lazy val authInfoRepository = new DelegableAuthInfoRepository(
    new CredentialAuthInfoRepository(database))


  lazy val passwordHasherRegistry = new PasswordHasherRegistry(
      new BCryptSha256PasswordHasher())

  lazy val credentialsProvider = new CredentialsProvider(
    authInfoRepository,
    passwordHasherRegistry)

  lazy val securedAction = new DefaultSecuredAction(
    new DefaultSecuredRequestHandler(
      new DefaultSecuredErrorHandler(messagesApi)),
    defaultBodyParsers)
  lazy val unsecuredAction = new DefaultUnsecuredAction(
    new DefaultUnsecuredRequestHandler(
      new DefaultUnsecuredErrorHandler(messagesApi)),
    defaultBodyParsers)
  lazy val userAwareAction = new DefaultUserAwareAction(
     new DefaultUserAwareRequestHandler(),
    defaultBodyParsers)
  lazy val silhouette = new SilhouetteProvider(silhouetteEnvironment,
    securedAction, unsecuredAction, userAwareAction)

  lazy val frontend = new Frontend(controllerComponents, assetsFinder,
    context.environment.mode)
  lazy val authSignUp = new SignUp(controllerComponents, database,
    silhouette, passwordHasherRegistry, authInfoRepository)
  lazy val authSignIn = new SignIn(controllerComponents, database,
    silhouette, credentialsProvider)
  lazy val profile = new Profile(controllerComponents,
    database, silhouette)

  lazy val readStories = new read.Stories(controllerComponents, database,
    readPlayer, silhouette)
  lazy val readScenes = new read.Scenes(controllerComponents, database,
    readPlayer, silhouette)
  lazy val readChoices = new read.Choices(controllerComponents, database,
    silhouette)
  lazy val readPlayer = new read.Player(controllerComponents, database,
    silhouette)
  lazy val writeStories = new write.Stories(controllerComponents, database,
    silhouette)
  lazy val writeScenes = new write.Scenes(controllerComponents, database,
    silhouette)

  lazy val router = new Router(actionBuilder, bodyParsers, frontend,
    authSignUp, authSignIn, profile,
    readStories, readScenes, readChoices, readPlayer,
    writeStories, writeScenes,
    assets)
}
