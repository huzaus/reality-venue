package co.reality.domain.output

import co.reality.domain.entity.{PlayerId, Venue, VenueId}
import co.reality.domain.error.VenueStorageError
import co.reality.domain.output.VenueStorage.{Service, VenueRepository}
import zio.{Has, IO, Ref, ULayer, ZLayer}

private[output] object InMemoryVenueStorage {

  type InMemoryState = Has[Ref[State]]

  val storage: ZLayer[InMemoryState, Nothing, VenueRepository] = ZLayer.fromFunction(state => new Service {
    override def save(venue: Venue): IO[VenueStorageError, Unit] =
      state.get.update(_.save(venue))

    override def update(id: VenueId, playerId: PlayerId): IO[VenueStorageError, Unit] =
      state.get.update(_.update(id, playerId))

    override def load(id: VenueId): IO[VenueStorageError, Option[Venue]] =
      state.get.modify(_.read(id))

    override def loadAll(): IO[VenueStorageError, Vector[Venue]] =
      state.get.modify(_.readAll())


  })

  val emptyState: ULayer[InMemoryState] = state(Map())

  def state(storage: Map[VenueId, Venue]): ULayer[InMemoryState] =
    Ref.make(State(storage)).toLayer


  sealed case class State(storage: Map[VenueId, Venue]) {
    def save(venue: Venue): State =
      copy(storage = storage.updated(venue.id, venue))

    def update(id: VenueId, playerId: PlayerId): State =
      copy(storage = storage.updatedWith(id)(_.map(_.copy(owner = Some(playerId)))))

    def readAll(): (Vector[Venue], State) =
      (storage.valuesIterator.toVector, this)

    def read(id        : VenueId): (Option[Venue], State) =
      (storage.get(id), this)
  }

}
