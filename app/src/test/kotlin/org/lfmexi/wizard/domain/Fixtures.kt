package org.lfmexi.wizard.domain

import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.LobbyGame
import org.lfmexi.wizard.domain.games.OngoingGame
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.rounds.DealingPhaseRound
import org.lfmexi.wizard.domain.rounds.RoundId
import org.lfmexi.wizard.domain.scoring.RoundScore

object Fixtures {
    val PLAYER_ID_1 = PlayerId.generate()
    val PLAYER_ID_2 = PlayerId.generate()
    val PLAYER_ID_3 = PlayerId.generate()

    val LOBBY_GAME = LobbyGame(
        id = GameId.generate(),
        players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3),
        owner = PLAYER_ID_1
    )

    val ONGOING_GAME = OngoingGame(
        id = GameId.generate(),
        players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3)
    )

    val DEALING_PHASE_ROUND = DealingPhaseRound(
        id = RoundId.generate(),
        gameId = ONGOING_GAME.id,
        roundNumber = ONGOING_GAME.ongoingRound,
        deck = ONGOING_GAME.deck,
        initialPlayer = ONGOING_GAME.players.first(),
        players = ONGOING_GAME.players,
        playerScoreBoard = ONGOING_GAME.players.map { it to RoundScore.ZERO_SCORE }.toMap(),
    )
}
