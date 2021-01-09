package org.lfmexi.wizard.domain.games

import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.rounds.Round

internal interface GameStateChanger {
    fun addPlayer(playerId: PlayerId): Game {
        throw UnsupportedOperationException("Cannot add a player to the game")
    }

    fun startGame(initiator: PlayerId): Game {
        throw UnsupportedOperationException("Start the game. It is not ready")
    }

    fun nextRound(): Game {
        throw UnsupportedOperationException("Cannot pull the next round. The game is not ready")
    }

    fun endGame(): Game {
        throw UnsupportedOperationException("Cannot end the game yet. The game is not ready")
    }
}
