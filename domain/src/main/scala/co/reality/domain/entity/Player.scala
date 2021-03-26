package co.reality.domain.entity

sealed case class Player(id: PlayerId, money: Long)

object Player {
  val player1 = Player(PlayerId("player1"), 500)
  val player2 = Player(PlayerId("player2"), 2000)
}
