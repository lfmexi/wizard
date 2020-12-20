package org.lfmexi.wizard.domain.rounds

import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.scoring.RoundScore

sealed class Turn {
    abstract val playerId: PlayerId
}

data class DeclaringTurn(
    override val playerId: PlayerId,
    val playerRoundScore: Map<PlayerId, RoundScore>
): Turn()

data class PlayingCardTurn(
    override val playerId: PlayerId
): Turn()