package co.reality.domain.input

import co.reality.domain.entity.Venue

import java.util.UUID

object VenueModule {

  trait Service {
    def create(venue: Venue) = ???

    def assign(id: UUID, playerId: String) = ???

    def find(id: UUID) = ???

    def findAll() = ???
  }

}
