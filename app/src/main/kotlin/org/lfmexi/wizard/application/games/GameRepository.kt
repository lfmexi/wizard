package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.games.GameId
import reactor.core.publisher.Mono

/**
 * Reactive [Game] Repository. Provides the repository operations
 * for [Game] persistence.
 */
interface GameRepository {
    /**
     * Saves the current state of a game
     */
    fun save(game: Game): Mono<Game>

    /**
     * Finds the game identified by its [GameId]
     */
    fun findById(id: GameId): Mono<Game>
}
