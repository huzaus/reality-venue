package co.reality.domain.input

import co.reality.domain.entity.{Player, PlayerId, Receipt, VenueId}
import co.reality.domain.error.{DomainError, PaymentError}
import co.reality.domain.input.VenueModule.VenueService
import zio.ZIO

object VenueShopModule {

  def sell(id: VenueId, playerId: PlayerId): ZIO[VenueService, DomainError, Receipt] =
    for {
      venue <- VenueModule.find(id)
      player <- ZIO.fromEither(Player.find(playerId))
      _ <- ZIO.fromEither(Either.cond(venue.price <= player.money,
                                      (),
                                      PaymentError.insufficientFunds(player, venue)))
      _ <- VenueModule.assign(id, playerId)
    } yield Receipt(player, venue)
}
