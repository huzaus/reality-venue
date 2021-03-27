package co.reality.api.route

import co.reality.api.route.VenueRoutes.context
import co.reality.api.util.ZioRoutes
import co.reality.api.util.ZioRoutes.ZioHttpRoutes
import co.reality.api.view.VenueRequest
import co.reality.domain.input.VenueModule
import co.reality.domain.input.VenueModule.VenueService
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import zio.{ULayer, ZIO}

object CreateVenueRoute {

  def apply(layer: ULayer[VenueService]): ZioHttpRoutes =
    ZioRoutes(definition, handler(_), layer)

  val definition: ZEndpoint[(String, VenueRequest), String, String] =
    endpoint.put
            .in(context / path[String]("id"))
            .in(jsonBody[VenueRequest])
            .errorOut(stringBody)
            .out(stringBody)

  def handler(input: (String, VenueRequest)): ZIO[VenueService, String, String] = {
    val (id, putVenue) = input
    for {
      venue <- ZIO.fromEither(putVenue.toDomainModel(id))
      _ <- VenueModule.create(venue).mapError(_.message)
    } yield venue.id.value.toString
  }
}
