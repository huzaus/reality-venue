package co.reality.domain.entity


final case class Venue(id: VenueId, name: String, price: Long, owner: Option[PlayerId])

