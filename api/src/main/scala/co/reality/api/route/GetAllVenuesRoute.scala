package co.reality.api.route

import co.reality.api.route.VenueRoutes.context
import co.reality.api.util.ZioRoutes
import co.reality.api.util.ZioRoutes.ZioHttpRoutes
import co.reality.api.view.Venue
import co.reality.domain.input.VenueModule
import co.reality.domain.input.VenueModule.VenueService
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import zio.{ULayer, ZIO}

object GetAllVenuesRoute {
  def apply(layer: ULayer[VenueService]): ZioHttpRoutes =
    ZioRoutes(definition, handler(_), layer)

  val definition: ZEndpoint[Unit, String, Vector[Venue]] =
    endpoint.get
            .in(context)
            .errorOut(stringBody)
            .out(jsonBody[Vector[Venue]])

  def handler(unit: Unit): ZIO[VenueService, String, Vector[Venue]] =
    VenueModule.findAll()
               .bimap(_.message, _.map(Venue(_)))

}
