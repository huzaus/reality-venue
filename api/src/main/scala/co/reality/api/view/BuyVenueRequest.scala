package co.reality.api.view

import co.reality.domain.entity.PlayerId
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class BuyVenueRequest(playerId: String) {
  def toPlayerId: PlayerId = PlayerId(playerId)
}

object BuyVenueRequest {
  implicit val buyVenueCodec: Codec[BuyVenueRequest] = deriveCodec
}