package co.reality.api.route

import co.reality.api.util.ZioRoutes.ZioHttpRoutes
import co.reality.api.util.{Swagger, ZioRoutes}
import co.reality.domain.input.VenueModule.VenueService
import org.http4s.HttpApp
import zio.clock.Clock
import zio.{RIO, ULayer}

object VenueRoutes {
  val context = "venues"

  def app(layer: ULayer[VenueService]): HttpApp[RIO[Clock, *]] =
    ZioRoutes.app(routes(layer))

  def routes(layer: ULayer[VenueService]): ZioHttpRoutes =
    ZioRoutes(CreateVenueRoute(layer),
              DeleteVenueRoute(layer),
              GetAllVenuesRoute(layer),
              swaggerRoutes)

  val swaggerRoutes: ZioHttpRoutes =
    Swagger.routes("Venue API", "1.0",
                   CreateVenueRoute.definition,
                   DeleteVenueRoute.definition,
                   GetAllVenuesRoute.definition)
}
