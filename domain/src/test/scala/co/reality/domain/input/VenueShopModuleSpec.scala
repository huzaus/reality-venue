package co.reality.domain.input

import co.reality.domain.entity.EntityGen.{venue, venueId}
import co.reality.domain.entity.Player.{player1, player2}
import co.reality.domain.entity.PlayerId
import co.reality.domain.error.{DomainError, PaymentError, PlayerNotFound, VenueNotFound}
import co.reality.domain.input.VenueModule.VenueService
import co.reality.domain.output.VenueStorage
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ULayer, ZEnv, ZIO}

class VenueShopModuleSpec extends AnyFlatSpec
  with Matchers
  with ScalaCheckPropertyChecks {

  val layer: ULayer[VenueService] = VenueStorage.inMemoryLayer >>> VenueModule.layer

  behavior of "VenueShopModule"

  it should "fail to sell non existing venue with VenueNotFound" in {
    val id = venueId.sample.get
    unsafeRun(VenueShopModule.sell(id, player1.id).either) shouldBe Left(VenueNotFound(id))
  }

  it should "fail to sell to unknown existing venue with VenueNotFound" in {
    val id = venueId.sample.get

    val playerId = PlayerId("unknown")

    val scenario = for {
      _ <- VenueModule.create(venue(id).sample.get)
      _ <- VenueShopModule.sell(id, playerId)
    } yield ()

    unsafeRun(scenario.either) shouldBe Left(PlayerNotFound(playerId))
  }

  it should "fail to sell venue due to insufficient funds" in {
    val price = Gen.chooseNum(player1.money + 1, Long.MaxValue)
    forAll(venue, price) { (generatedVenue, price) =>
      val venue = generatedVenue.copy(price = price)
      val scenario     = for {
        _ <- VenueModule.create(venue)
        _ <- VenueShopModule.sell(venue.id, player1.id)
      } yield ()
      unsafeRun(scenario.either) shouldBe Left(PaymentError.insufficientFunds(player1, venue))
    }
  }

  it should "successfully sell venue due to the player" in {
    val price = Gen.chooseNum(1, player2.money)
    forAll(venue, price) { (generatedVenue, price) =>
      val venue = generatedVenue.copy(price = price)
      val scenario     = for {
        _ <- VenueModule.create(venue)
        receipt <- VenueShopModule.sell(venue.id, player2.id)
        updatedVenue <- VenueModule.find(venue.id)
      } yield (receipt, updatedVenue)

      val (receipt, updatedVenue) = unsafeRun(scenario)

      receipt.venue shouldBe venue
      receipt.player shouldBe player2
      updatedVenue shouldBe venue.copy(owner = Some(player2.id))
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with VenueService, DomainError, T]): T = {
    default.unsafeRun(scenario.provideCustomLayer(layer))
  }
}
