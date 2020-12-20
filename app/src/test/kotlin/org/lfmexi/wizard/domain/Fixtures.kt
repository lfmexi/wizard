package org.lfmexi.wizard.domain

import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.LobbyGame
import org.lfmexi.wizard.domain.players.PlayerId

object Fixtures {
    val PLAYER_ID_1 = PlayerId.generate()
    val PLAYER_ID_2 = PlayerId.generate()
    val PLAYER_ID_3 = PlayerId.generate()

    val LOBBY_GAME = LobbyGame(
        id = GameId.generate(),
        players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3),
        owner = PLAYER_ID_1
    )
}