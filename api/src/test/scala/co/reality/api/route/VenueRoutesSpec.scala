package co.reality.api.route

import co.reality.api.view.ViewGen._
import co.reality.api.view.{BuyVenueRequest, Venue}
import co.reality.domain.entity.Player.{player1, player2}
import co.reality.domain.input.VenueModule
import co.reality.domain.input.VenueModule.VenueService
import org.http4s.HttpApp
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.clock.Clock
import zio.{RIO, Runtime, ULayer}

import java.util.UUID

class VenueRoutesSpec extends AnyFlatSpec
  with Matchers
  with VenueBehaviours
  with ScalaCheckPropertyChecks {

  val layer: ULayer[VenueService] = VenueModule.inMemoryLayer

  val app: HttpApp[RIO[Clock, *]] = VenueRoutes.app(layer)

  val runtime: Runtime[zio.ZEnv] = Runtime.default

  behavior of "VenueRoutes"

  it should "put and get venue" in {
    forAll(venueRequest) { request =>
      val id       = UUID.randomUUID().toString
      val scenario = for {
        uuid <- put(id, request)
        venues <- getAll()
      } yield (uuid.success, venues.success)

      val (uuid, venues) = runtime.unsafeRun(scenario)

      uuid shouldBe id
      venues should contain(Venue(id, request.name, request.price, None))
    }
  }

  it should "put and delete venue" in {
    forAll(venueRequest) { request =>
      val id       = UUID.randomUUID().toString
      val scenario = for {
        putUuid <- put(id, request)
        deleteUuid <- delete(id)
        venues <- getAll()
      } yield (putUuid.success, deleteUuid.success, venues.success)

      val (putUuid, deleteUuid, venues) = runtime.unsafeRun(scenario)

      putUuid shouldBe id
      deleteUuid shouldBe id
      venues.map(_.id) should not contain id
    }
  }

  it should "fail to buy venue due to insufficient funds" in {
    val price = Gen.chooseNum(player1.money + 1, Long.MaxValue)
    forAll(venueRequest, price) { (generatedVenue, price) =>
      val request = generatedVenue.copy(price = price)

      val id = UUID.randomUUID().toString

      val scenario = for {
        _ <- put(id, request).map(_.success)
        receipt <- buy(id, BuyVenueRequest(player1.id.value))
      } yield receipt.failure

      runtime.unsafeRun(scenario) shouldBe s"${player1.id.value} can't afford ${request.name}"
    }
  }

  it should "buy venue successfully" in {
    val price = Gen.chooseNum(1, player2.money)
    forAll(venueRequest, price) { (generatedVenue, price) =>
      val request = generatedVenue.copy(price = price)

      val id = UUID.randomUUID().toString

      val scenario = for {
        _ <- put(id, request).map(_.success)
        receipt <- buy(id, BuyVenueRequest(player2.id.value))
        venues <- getAll()
      } yield (receipt.success, venues.success)

      val (receipt, venues) = runtime.unsafeRun(scenario)

      receipt shouldBe s"${request.name} was bought by ${player2.id.value} for ${request.price}"

      venues should contain(Venue(id, request.name, request.price, Some(player2.id.value)))
    }
  }

}
