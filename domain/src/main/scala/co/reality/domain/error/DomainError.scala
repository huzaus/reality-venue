package co.reality.domain.error

sealed trait DomainError {
  def message: String
}

final case class VenueStorageError(message: String) extends DomainError
