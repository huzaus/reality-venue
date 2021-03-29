package co.reality.api.view

import co.reality.domain.entity
import co.reality.domain.entity.{PlayerId, VenueId}
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class PutVenueRequest(name: String, price: Long) {
  def toDomainModel(id: String): Either[String, entity.Venue] =
    VenueId.fromString(id)
           .map(entity.Venue(_, name, price, None))
}

object PutVenueRequest {
  implicit val putVenueCodec: Codec[PutVenueRequest] = deriveCodec
}