package co.reality.domain.entity

final case class Receipt(player: Player,
                         venue : Venue) {
  val text: String =
    s"${venue.name} was bought by ${player.id.value} for ${venue.price}"
}
