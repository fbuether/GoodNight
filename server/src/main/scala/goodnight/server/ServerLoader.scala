
package goodnight.server

import play.api.ApplicationLoader
import play.api.Application
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.LoggerConfigurator
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFComponents
import controllers.AssetsComponents

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
import goodnight.api.UserService
import goodnight.api.JWTEnv
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
    with AssetsComponents
    with CSRFComponents {
  lazy val bodyParsers = PlayBodyParsers()
  lazy val actionBuilder = DefaultActionBuilder(bodyParsers.defaultBodyParser)

  lazy val silhouetteEnvironment = Environment[JWTEnv](
    new UserService(),

    new JWTAuthenticatorService(
      JWTAuthenticatorSettings(sharedSecret = "I-R-SECRET"),
      None, // repository: Option[AuthenticatorRepository[JWTAuthenticator]],
      new Base64AuthenticatorEncoder(),
      new SecureRandomIDGenerator(),
      Clock()),
    Seq(), // requestProvidersImpl: Seq[RequestProvider]
    EventBus()
    // identityServiceImpl: IdentityService[Environment.apply.E.I],
    // authenticatorServiceImpl: AuthenticatorService[Environment.apply.E.A],
    // requestProvidersImpl: Seq[RequestProvider],
    // eventBusImpl: EventBus)(implicit ec: ExecutionContext)
)
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
  lazy val profile = new Profile(controllerComponents, silhouette)
  lazy val router = new Router(actionBuilder, bodyParsers, frontend,
    authentication, profile, assets)
}
