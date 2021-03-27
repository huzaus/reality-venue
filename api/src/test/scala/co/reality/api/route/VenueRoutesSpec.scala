package co.reality.api.route

import co.reality.api.view.Venue
import co.reality.api.view.ViewGen._
import co.reality.domain.input.VenueModule
import co.reality.domain.input.VenueModule.VenueService
import org.http4s.HttpApp
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

  val runtime = Runtime.default

  behavior of "VenueRoutes"

  it should "get empty result for empty state" in {
    runtime.unsafeRun(getAll()).success shouldBe empty
  }

  it should "put and get venue" in {
    forAll(venueRequest) { request =>
      val id = UUID.randomUUID().toString
      val scenario = for {
        uuid <- putVenue(id, request)
        venues <- getAll()
      } yield (uuid.success, venues.success)

      val (uuid, venues) = runtime.unsafeRun(scenario)

      uuid shouldBe id
      venues should contain(Venue(id, request.name, request.price, None))
    }
  }

}
