package co.reality.domain.output

import co.reality.domain.entity.EntityGen.{playerId, venue, _}
import co.reality.domain.error.VenueStorageError
import co.reality.domain.output.VenueStorage.VenueRepository
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ULayer, ZEnv, ZIO}

class VenueStorageSpec extends AnyFlatSpec
  with Matchers
  with ScalaCheckPropertyChecks {

  behavior of "VenueStorage"

  val layer: ULayer[VenueRepository] = VenueStorage.inMemoryLayer

  it should "return empty result for non existing venue id" in {
    val id = venueId.sample.get
    unsafeRun(VenueStorage.load(id)) shouldBe None
  }

  it should "save and load venue" in {
    forAll(venue) { venue =>
      val scenario = for {
        _ <- VenueStorage.save(venue)
        venue <- VenueStorage.load(venue.id)
      } yield venue

      unsafeRun(scenario) should contain(venue)
    }
  }

  it should "save and load all venues" in {
    forAll(Gen.listOf(venue)) { venues =>
      val scenario = for {
        _ <- ZIO.foreach_(venues)(VenueStorage.save)
        venues <- VenueStorage.loadAll()
      } yield venues

      unsafeRun(scenario) should contain theSameElementsAs venues
    }
  }

  it should "update venue with player id" in {
    forAll(venue, playerId) { (venue, playerId) =>
      val scenario = for {
        _ <- VenueStorage.save(venue)
        _ <- VenueStorage.update(venue.id, playerId)
        venue <- VenueStorage.load(venue.id)
      } yield venue

      unsafeRun(scenario) should contain(venue.copy(owner = Some(playerId)))
    }
  }

  it should "overwrite existing venue" in {
    val id = venueId.sample.get
    forAll(venue(id), venue(id)) { (origin, update) =>
      val scenario = for {
        _ <- VenueStorage.save(origin)
        _ <- VenueStorage.save(update)
        venue <- VenueStorage.load(id)
      } yield venue

      unsafeRun(scenario) should contain(update)
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with VenueRepository, VenueStorageError, T]): T = {
    default.unsafeRun(scenario.provideCustomLayer(layer))
  }
}
