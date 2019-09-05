
package goodnight.server

import play.api.routing.SimpleRouter
import play.api.routing.Router.Routes
import play.api.mvc.DefaultActionBuilder
import play.api.mvc.PlayBodyParsers
import play.api.mvc.Results
import play.api.routing._
import play.api.routing.sird._

import goodnight.client.Frontend

// import controllers.Assets

class Router(
  action: DefaultActionBuilder,
  parse: PlayBodyParsers,
  frontend: Frontend
)
    extends SimpleRouter {

  // lazy val assets = new Assets

  // PartialFunction[RequestHeader, Handler]
  def routes: Routes = {
    case GET(p"/") => frontend.html


// action(parse.text) {
//       // Results.Ok("okay.")
//     }
  }

}



// class Routes(
//   override val errorHandler: play.api.http.HttpErrorHandler,
//   Application_0: controllers.Application,
//   bar_Routes_0: bar.Routes,
//   Assets_1: controllers.Assets,
//   val prefix: String
// ) extends GeneratedRouter {

//   def this(
//     errorHandler: play.api.http.HttpErrorHandler,
//     Application_0: controllers.Application,
//     bar_Routes_0: bar.Routes,
//     Assets_1: controllers.Assets
//   ) = this(Application_0, bar_Routes_0, Assets_1, "/")
//   ...
// }
