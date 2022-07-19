package org.lfmexi.wizard.games.application

import org.lfmexi.wizard.games.application.exceptions.GameNotFoundException
import org.lfmexi.wizard.games.domain.Game
import org.lfmexi.wizard.games.domain.GameId
import reactor.core.publisher.Mono

class GameFetcherService internal constructor(
    private val gameRepository: GameRepository
) {
    fun getGame(gameId: GameId): Mono<Game> {
        return gameRepository.findById(gameId)
            .switchIfEmpty(Mono.defer {
                Mono.error(GameNotFoundException("Game with id $gameId doesn't exist"))
            })
    }
}
