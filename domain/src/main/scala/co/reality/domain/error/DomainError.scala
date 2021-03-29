package co.reality.domain.error

import co.reality.domain.entity.{Player, PlayerId, Venue, VenueId}

sealed trait DomainError {
  def message: String
}

final case class VenueStorageError(message: String) extends DomainError

final case class VenueNotFound(id: VenueId) extends DomainError {
  override def message: String = s"$id is not found"
}

final case class PlayerNotFound(id: PlayerId) extends DomainError {
  override def message: String = s"$id is not found"
}

sealed case class PaymentError(message: String) extends DomainError

object PaymentError {
  def insufficientFunds(player: Player, venue: Venue): PaymentError =
    PaymentError(s"${player.id.value} can't afford ${venue.name}")
}