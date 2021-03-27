package co.reality.api.view

import co.reality.domain.entity.EntityGen

object ViewGen {

  val venueRequest = for {
    name <- EntityGen.name
    price <- EntityGen.money
  } yield VenueRequest(name, price)
}
