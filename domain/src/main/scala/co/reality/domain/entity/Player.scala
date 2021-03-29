package co.reality.domain.entity

import cats.syntax.either._
import co.reality.domain.error.PlayerNotFound

sealed case class Player(id: PlayerId, money: Long)

object Player {
  val player1 = Player(PlayerId("player1"), 500)
  val player2 = Player(PlayerId("player2"), 2000)

  def find(playerId: PlayerId): Either[PlayerNotFound, Player] = playerId match {
    case player1.id => player1.asRight
    case player2.id => player2.asRight
    case _          => PlayerNotFound(playerId).asLeft
  }
}
