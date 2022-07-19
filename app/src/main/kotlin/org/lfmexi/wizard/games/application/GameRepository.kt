package org.lfmexi.wizard.games.application

import org.lfmexi.wizard.games.domain.Game
import org.lfmexi.wizard.games.domain.GameId
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
