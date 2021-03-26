package co.reality.domain.error

import co.reality.domain.entity.VenueId

sealed trait DomainError {
  def message: String
}

final case class VenueStorageError(message: String) extends DomainError

final case class VenueNotFound(id: VenueId) extends DomainError {
  override def message: String = s"$id is not found"
}