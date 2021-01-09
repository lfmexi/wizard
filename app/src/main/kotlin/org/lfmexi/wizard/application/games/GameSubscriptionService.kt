package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.exceptions.StartedGameNotFoundException
import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.games.EndedGame
import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.games.GameId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class GameSubscriptionService internal constructor(
    private val gameRepository: GameRepository,
    private val gamePubSubChannel: GamePubSubChannel
) {
    /**
     * Provides a subscription to the game
     */
    fun subscribeToGame(gameId: GameId): Flux<DomainEvent> {
        return getStartedGame(gameId)
            .thenMany(gamePubSubChannel.subscribeById(gameId))
    }

    private fun getStartedGame(gameId: GameId): Mono<Game> {
        return gameRepository.findById(gameId)
            .flatMap {
                if (it is EndedGame) {
                    Mono.empty()
                } else {
                    Mono.just(it)
                }
            }
            .switchIfEmpty(Mono.defer { Mono.error(StartedGameNotFoundException(gameId)) })
    }
}
