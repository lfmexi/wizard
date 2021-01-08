package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.exceptions.OngoingGameNotFoundException
import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.games.GameEvent
import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.OngoingGame
import reactor.core.publisher.Mono

class OngoingGameService internal constructor(
    private val gameFetcherService: GameFetcherService,
    private val gameRepository: GameRepository,
    private val gameEventPublisher: EventPublisher<GameEvent>
){
    fun updateForNextRound(gameId: GameId): Mono<Game> {
        return getOngoingGame(gameId)
            .map {
                it.nextRound()
            }
            .flatMap {
                gameRepository.save(it)
            }
            .doOnSuccess {
                gameEventPublisher.publishEvents(it.recordedEvents)
            }
    }

    private fun getOngoingGame(gameId: GameId): Mono<Game> {
        return gameFetcherService.getGame(gameId)
            .flatMap {
                if (it is OngoingGame) {
                    Mono.just(it)
                } else {
                    Mono.error(OngoingGameNotFoundException("Game $gameId cannot be found as an ongoing game"))
                }
            }
    }
}
