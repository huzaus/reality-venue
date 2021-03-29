package co.reality.api.route

import co.reality.api.route.VenueRoutes.context
import co.reality.api.util.ZioRoutes
import co.reality.api.util.ZioRoutes.ZioHttpRoutes
import co.reality.api.view.{BuyVenueRequest, PutVenueRequest}
import co.reality.domain.entity.{Receipt, VenueId}
import co.reality.domain.input.{VenueModule, VenueShopModule}
import co.reality.domain.input.VenueModule.VenueService
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import zio.{ULayer, ZIO}

object BuyVenueRoute {

  def apply(layer: ULayer[VenueService]): ZioHttpRoutes =
    ZioRoutes(definition, handler(_), layer)

  val definition: ZEndpoint[(String, BuyVenueRequest), String, String] =
    endpoint.post
            .in(context / path[String]("id"))
            .in(jsonBody[BuyVenueRequest])
            .errorOut(stringBody)
            .out(stringBody)

  def handler(input: (String, BuyVenueRequest)): ZIO[VenueService, String, String] = {
    val (id, buyVenue) = input
    for {
      id <- ZIO.fromEither(VenueId.fromString(id))
      receipt <- VenueShopModule.sell(id, buyVenue.toPlayerId).mapError(_.message)
    } yield receipt.text
  }
}
