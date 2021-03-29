package co.reality.domain.entity

import org.scalacheck.Gen

object EntityGen {

  val venueId: Gen[VenueId] = Gen.uuid.map(uuid => VenueId(uuid))

  val minNameSize = 3
  val maxNameSize = 1000

  val name = for {
    size <- Gen.chooseNum(minNameSize, maxNameSize)
    name <- Gen.listOfN(size, Gen.alphaChar)
  } yield name.mkString

  val playerId = name.map(name => PlayerId(name))

  val money: Gen[Long] = Gen.posNum[Long]

  val venue: Gen[Venue] = venue()

  def venue(venueId: Gen[VenueId] = venueId): Gen[Venue] = for {
    id <- venueId
    name <- name
    price <- money
    owner <- Gen.option(playerId)
  } yield Venue(id, name, price, owner)
}
