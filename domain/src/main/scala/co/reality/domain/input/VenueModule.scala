package co.reality.domain.input

import co.reality.domain.entity.{PlayerId, Venue, VenueId}
import co.reality.domain.error.{DomainError, VenueNotFound}
import co.reality.domain.output.VenueStorage
import co.reality.domain.output.VenueStorage.VenueRepository
import zio.{Has, IO, ULayer, URLayer, ZIO, ZLayer}

object VenueModule {

  type VenueService = Has[Service]

  trait Service {
    def create(venue: Venue): IO[DomainError, Unit]

    def assign(id: VenueId, playerId: PlayerId): IO[DomainError, Unit]

    def find(id: VenueId): IO[DomainError, Venue]

    def findAll(): IO[DomainError, Vector[Venue]]
  }

  def create(venue: Venue): ZIO[VenueService, DomainError, Unit] =
    ZIO.accessM[VenueService](_.get[Service].create(venue))

  def assign(id: VenueId, playerId: PlayerId): ZIO[VenueService, DomainError, Unit] =
    ZIO.accessM[VenueService](_.get[Service].assign(id, playerId))

  def find(id: VenueId): ZIO[VenueService, DomainError, Venue] =
    ZIO.accessM[VenueService](_.get[Service].find(id))

  def findAll(): ZIO[VenueService, DomainError, Vector[Venue]] =
    ZIO.accessM[VenueService](_.get[Service].findAll())

  val layer: URLayer[VenueRepository, VenueService] = {
    ZLayer.fromService[VenueStorage.Service, Service] { venueStorage =>
      new Service {
        override def create(venue: Venue): IO[DomainError, Unit] =
          venueStorage.save(venue)

        override def assign(id: VenueId, playerId: PlayerId): IO[DomainError, Unit] = for {
          _ <- find(id)
          _ <- venueStorage.update(id, playerId)
        } yield ()


        override def find(id: VenueId): IO[DomainError, Venue] = for {
          venueOption <- venueStorage.load(id)
          venue <- ZIO.fromEither(venueOption.toRight(VenueNotFound(id)))
        } yield venue

        override def findAll(): IO[DomainError, Vector[Venue]] =
          venueStorage.loadAll()
      }
    }
  }

  val inMemoryLayer: ULayer[VenueService] = VenueStorage.inMemoryLayer >>> layer
}
