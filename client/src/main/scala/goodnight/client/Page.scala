
package goodnight.client

import japgolly.scalajs.react.extra.router.{RouterConfigDsl, StaticDsl}


trait Page {
  def route(dsl: RouterConfigDsl[Pages.Page]): StaticDsl.Rule[Pages.Page]
}
