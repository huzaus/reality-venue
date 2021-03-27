package co.reality.api.util

import co.reality.api.util.ZioRoutes.ZioHttpRoutes
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.ztapir.ZEndpoint
import zio.interop.catz._

object Swagger {

  def routes(title      : String,
             version    : String,
             definition : ZEndpoint[_, _, _], rest: ZEndpoint[_, _, _]*): ZioHttpRoutes =
    routes(title, version, definition +: rest)


  def routes(title      : String,
             version    : String,
             definitions: Seq[ZEndpoint[_, _, _]]): ZioHttpRoutes = {
    val yaml: String = {
      import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
      import sttp.tapir.openapi.circe.yaml._
      OpenAPIDocsInterpreter.toOpenAPI(definitions, title, version).toYaml
    }
    new SwaggerHttp4s(yaml).routes
  }

}
