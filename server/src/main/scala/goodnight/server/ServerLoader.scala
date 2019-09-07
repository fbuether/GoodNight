
package goodnight.server

import play.api.ApplicationLoader
import play.api.Application
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.LoggerConfigurator
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFComponents
import controllers.AssetsComponents
import play.api.db.slick.SlickComponents
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents

import play.api.mvc.DefaultActionBuilder
import play.api.mvc.PlayBodyParsers
import play.api.mvc.BodyParsers

import com.mohiva.play.silhouette.api.SilhouetteProvider
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.actions.DefaultSecuredAction
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredAction
import com.mohiva.play.silhouette.api.actions.DefaultUserAwareAction
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticatorService
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticatorSettings
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.actions.DefaultSecuredRequestHandler
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredRequestHandler
import com.mohiva.play.silhouette.api.actions.DefaultUserAwareRequestHandler
import com.mohiva.play.silhouette.api.actions.DefaultSecuredErrorHandler
import com.mohiva.play.silhouette.api.actions.DefaultUnsecuredErrorHandler

import goodnight.client.Frontend
import goodnight.api.Authentication
import goodnight.api.authentication.SignUp
import goodnight.api.authentication.SignIn
import goodnight.api.authentication.UserService
import goodnight.api.authentication.JwtEnvironment
import goodnight.api.Profile


class ServerLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach({
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    })
    new GoodnightComponents(context).application
  }
}


class GoodnightComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with SlickComponents
    with SlickEvolutionsComponents
    with EvolutionsComponents
    with AssetsComponents
    with CSRFComponents {

  // run the database evolution scripts
  applicationEvolutions

  lazy val bodyParsers = PlayBodyParsers()
  lazy val actionBuilder = DefaultActionBuilder(bodyParsers.defaultBodyParser)

  lazy val database = slickApi.dbConfigs[PostgresProfile]().head._2.db


  lazy val userService = new UserService(database)

  lazy val jwtSharedSecret =
    "7e1d6bfe49f744c62096826006b8a8c2fe242cc3ca511f8bc850512f138c935d"
  lazy val silhouetteEnvironment = Environment[JwtEnvironment](
    userService,
    new JWTAuthenticatorService(
      JWTAuthenticatorSettings(sharedSecret = jwtSharedSecret),
      None, // repository: Option[AuthenticatorRepository[JWTAuthenticator]],
      new Base64AuthenticatorEncoder(),
      new SecureRandomIDGenerator(),
      Clock()),
    Seq(), // requestProvidersImpl: Seq[RequestProvider]
    EventBus())
  lazy val defaultBodyParsers = new BodyParsers.Default(bodyParsers)

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

  lazy val frontend = new Frontend(controllerComponents, assetsFinder)
  lazy val authentication = new Authentication(controllerComponents, silhouette)
  lazy val authSignUp = new SignUp(controllerComponents, database,
    silhouette)
  lazy val authSignIn = new SignIn(controllerComponents,
    silhouette)
  lazy val profile = new Profile(controllerComponents, userService,
    database,
    silhouette)
  lazy val router = new Router(actionBuilder, bodyParsers, frontend,
    authentication, authSignUp, authSignIn,
    profile, assets)
}
