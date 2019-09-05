
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

import goodnight.client.Frontend


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

  lazy val frontend = new Frontend(controllerComponents, assetsFinder)
  lazy val router = new Router(actionBuilder, bodyParsers, frontend, assets)
}
