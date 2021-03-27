package co.reality.domain.output

import co.reality.domain.entity.{PlayerId, Venue, VenueId}
import co.reality.domain.error.VenueStorageError
import zio.{Has, IO, ULayer, ZIO}

object VenueStorage {

  type VenueRepository = Has[Service]

  trait Service {
    def save(venue: Venue): IO[VenueStorageError, Unit]

    def update(id: VenueId, playerId: PlayerId): IO[VenueStorageError, Unit]

    def load(id: VenueId): IO[VenueStorageError, Option[Venue]]

    def loadAll(): IO[VenueStorageError, Vector[Venue]]
  }

  def save(venue: Venue): ZIO[VenueRepository, VenueStorageError, Unit] =
    ZIO.accessM[VenueRepository](_.get[Service].save(venue))

  def update(id: VenueId, playerId: PlayerId): ZIO[VenueRepository, VenueStorageError, Unit] =
    ZIO.accessM[VenueRepository](_.get[Service].update(id, playerId))

  def load(id: VenueId): ZIO[VenueRepository, VenueStorageError, Option[Venue]] =
    ZIO.accessM[VenueRepository](_.get[Service].load(id))

  def loadAll(): ZIO[VenueRepository, VenueStorageError, Vector[Venue]] =
    ZIO.accessM[VenueRepository](_.get[Service].loadAll())


  val inMemoryLayer: ULayer[VenueRepository] =
    InMemoryVenueStorage.emptyState >>> InMemoryVenueStorage.storage

  val inMemorySharedLayer: ULayer[VenueRepository] =
    InMemoryVenueStorage.sharedState >>> InMemoryVenueStorage.storage
}
