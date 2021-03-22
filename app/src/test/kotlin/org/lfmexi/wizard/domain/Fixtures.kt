package org.lfmexi.wizard.domain

import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.LobbyGame
import org.lfmexi.wizard.domain.games.OngoingGame
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.games.rounds.DealingPhaseRound
import org.lfmexi.wizard.domain.games.rounds.DeclarationPhaseRound
import org.lfmexi.wizard.domain.games.rounds.PlayingPhaseRound
import org.lfmexi.wizard.domain.games.rounds.RoundId
import org.lfmexi.wizard.domain.scoring.RoundScore
import org.lfmexi.wizard.domain.values.NumericValue

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
        ongoingRoundNumber = NumericValue.ONE,
        players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3)
    )

    val DEALING_PHASE_ROUND = DealingPhaseRound(
        id = RoundId.generate(),
        gameId = ONGOING_GAME.id,
        roundNumber = ONGOING_GAME.ongoingRoundNumber,
        dealingPlayer = ONGOING_GAME.players.first(),
        players = ONGOING_GAME.players,
        playerScoreBoard = ONGOING_GAME.players.map { it to RoundScore.ZERO_SCORE }.toMap(),
    )

    val DECLARATION_PHASE_ROUND = DeclarationPhaseRound(
        id = RoundId.generate(),
        gameId = ONGOING_GAME.id,
        roundNumber = ONGOING_GAME.ongoingRoundNumber,
        players = ONGOING_GAME.players,
        playerScoreBoard = ONGOING_GAME.players.map { it to RoundScore.ZERO_SCORE }.toMap(),
        dealingPlayer = ONGOING_GAME.players.first(),
        initialPlayer = ONGOING_GAME.players[1],
        currentPlayer = ONGOING_GAME.players[1],
        referenceCardGroup = null,
        hands = emptyList()
    )

    val PLAYING_PHASE_ROUND = PlayingPhaseRound(
        id = RoundId.generate(),
        gameId = ONGOING_GAME.id,
        roundNumber = ONGOING_GAME.ongoingRoundNumber,
        players = ONGOING_GAME.players,
        playerScoreBoard = ONGOING_GAME.players.map { it to RoundScore.ZERO_SCORE }.toMap(),
        dealingPlayer = ONGOING_GAME.players.first(),
        initialPlayer = ONGOING_GAME.players[1],
        currentPlayer = ONGOING_GAME.players[1],
        triumphCardGroup = null,
        currentWinningPlayer = ONGOING_GAME.players[1],
        currentWinningCard = null,
        playingCardGroup = null,
        triumphsPlayed = NumericValue.ZERO,
        hands = emptyList()
    )
}
