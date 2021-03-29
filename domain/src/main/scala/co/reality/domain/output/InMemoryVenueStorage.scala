package co.reality.domain.output

import co.reality.domain.entity.{PlayerId, Venue, VenueId}
import co.reality.domain.error.VenueStorageError
import co.reality.domain.output.VenueStorage.{Service, VenueRepository}
import zio.Runtime.default
import zio.{Has, IO, Ref, UIO, ULayer, ZLayer}

private[output] object InMemoryVenueStorage {

  type InMemoryState = Has[Ref[State]]

  val storage: ZLayer[InMemoryState, Nothing, VenueRepository] = ZLayer.fromFunction(state => new Service {
    override def save(venue: Venue): IO[VenueStorageError, Unit] =
      state.get.update(_.save(venue))

    override def delete(id: VenueId): IO[VenueStorageError, Unit] =
      state.get.update(_.delete(id))

    override def update(id: VenueId, playerId: PlayerId): IO[VenueStorageError, Unit] =
      state.get.update(_.update(id, playerId))

    override def load(id: VenueId): IO[VenueStorageError, Option[Venue]] =
      state.get.modify(_.read(id))

    override def loadAll(): IO[VenueStorageError, Vector[Venue]] =
      state.get.modify(_.readAll())

  })

  val sharedState: ULayer[InMemoryState] = ZLayer.succeed(default.unsafeRun(state(Map())))

  val emptyState: ULayer[InMemoryState] = state(Map()).toLayer

  def state(storage: Map[VenueId, Venue]): UIO[Ref[State]] =
    Ref.make(State(storage))


  sealed case class State(storage: Map[VenueId, Venue]) {
    def save(venue: Venue): State =
      copy(storage = storage.updated(venue.id, venue))

    def delete(id: VenueId): State =
      copy(storage = storage - id)

    def update(id: VenueId, playerId: PlayerId): State =
      copy(storage = storage.updatedWith(id)(_.map(_.copy(owner = Some(playerId)))))

    def readAll(): (Vector[Venue], State) =
      (storage.valuesIterator.toVector, this)

    def read(id: VenueId): (Option[Venue], State) =
      (storage.get(id), this)
  }

}
