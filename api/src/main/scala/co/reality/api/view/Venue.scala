package co.reality.api.view

import co.reality.domain.entity
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Venue(id: String, name: String, price: Long, owner: Option[String])

object Venue {
  def apply(venue: entity.Venue): Venue =
    Venue(venue.id.value.toString, venue.name, venue.price, venue.owner.map(_.value))

  implicit val venueCodec1: Encoder[Venue] = deriveEncoder[Venue].mapJson(_.dropNullValues)
  implicit val venueCodec2: Decoder[Venue] = deriveDecoder
}