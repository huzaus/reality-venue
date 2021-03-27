package co.reality.api.view

import co.reality.domain.entity
import co.reality.domain.entity.VenueId
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class VenueRequest(name: String, price: Long) {
  def toDomainModel(id: String): Either[String, entity.Venue] =
    VenueId.fromString(id)
           .map(entity.Venue(_, name, price, None))
}

object VenueRequest {
  implicit val putVenueCodec: Codec[VenueRequest] = deriveCodec
}