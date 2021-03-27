package co.reality.api.view

import co.reality.domain.entity
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Venue(id: String, name: String, price: Long, owner: Option[String])

object Venue {
  def apply(venue: entity.Venue): Venue =
    Venue(venue.id.value.toString, venue.name, venue.price, venue.owner.map(_.value))

  implicit val venueCodec: Codec[Venue] = deriveCodec
}