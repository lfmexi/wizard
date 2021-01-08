package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.exceptions.GameNotFoundException
import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.games.GameId
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
