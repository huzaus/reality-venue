package co.reality.domain.input

import co.reality.domain.entity.EntityGen.{playerId, _}
import co.reality.domain.error.{DomainError, VenueNotFound}
import co.reality.domain.input.VenueModule.VenueService
import co.reality.domain.output.VenueStorage
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ULayer, ZEnv, ZIO}

class VenueModuleSpec extends AnyFlatSpec
  with Matchers
  with ScalaCheckPropertyChecks {

  val layer: ULayer[VenueService] = VenueStorage.inMemoryLayer >>> VenueModule.layer

  behavior of "VenueModule"

  it should "return VenueNotFound error for non-existing venue id" in {
    val id = venueId.sample.get
    unsafeRun(VenueModule.find(id).either) shouldBe Left(VenueNotFound(id))
  }

  it should "return empty result for empty storage" in {
    unsafeRun(VenueModule.findAll()) shouldBe empty
  }

  it should "create and find venue" in {
    forAll(venue) { venue =>
      val scenario = for {
        _ <- VenueModule.create(venue)
        venue <- VenueModule.find(venue.id)
      } yield venue

      unsafeRun(scenario) shouldBe venue
    }
  }

  it should "update venue with player id" in {
    forAll(venue, playerId) { (venue, playerId) =>
      val scenario = for {
        _ <- VenueModule.create(venue)
        _ <- VenueModule.assign(venue.id, playerId)
        venue <- VenueModule.find(venue.id)
      } yield venue

      unsafeRun(scenario) shouldBe venue.copy(owner = Some(playerId))
    }
  }

  it should "create and find all venues" in {
    forAll(Gen.listOf(venue)) { venues =>
      val scenario = for {
        _ <- ZIO.foreach_(venues)(VenueModule.create)
        venues <- VenueModule.findAll()
      } yield venues

      unsafeRun(scenario) should contain theSameElementsAs venues
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with VenueService, DomainError, T]): T = {
    default.unsafeRun(scenario.provideCustomLayer(layer))
  }
}
