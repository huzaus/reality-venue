package co.reality.api.route

import co.reality.api.route.VenueRoutes.context
import co.reality.api.util.ZioRoutes
import co.reality.api.util.ZioRoutes.ZioHttpRoutes
import co.reality.domain.entity.VenueId
import co.reality.domain.input.VenueModule.VenueService
import sttp.tapir.ztapir._
import zio.{ULayer, ZIO}

object DeleteVenueRoute {
  def apply(layer: ULayer[VenueService]): ZioHttpRoutes =
    ZioRoutes(definition, handler(_), layer)

  val definition: ZEndpoint[String, String, String] =
    endpoint.delete
            .in(context / path[String]("id"))
            .errorOut(stringBody)
            .out(stringBody)

  def handler(venueId: String): ZIO[VenueService, String, String] = for {
    id <- ZIO.fromEither(VenueId.fromString(venueId))
    //    _ <- VenueModule.delete(id).mapError(_.message) //TODO
  } yield venueId
}
