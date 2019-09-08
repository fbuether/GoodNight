
package goodnight.client

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method._



object Request {
  private val baseUrl = "http://localhost:9000"


  def get(url: String): HttpRequest =
    HttpRequest(baseUrl + url).
      withMethod(GET).
      withHeader("Accept", "text/json")// .
      // withHeader("Csrf-Token", )

  def post(url: String): HttpRequest =
    HttpRequest(baseUrl + url).
      withMethod(POST).
      withHeader("Accept", "text/json")// .
      // withHeader("Csrf-Token", )

}
