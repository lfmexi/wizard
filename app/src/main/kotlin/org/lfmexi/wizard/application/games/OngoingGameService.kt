package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.exceptions.OngoingGameNotFoundException
import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.OngoingGame
import org.lfmexi.wizard.domain.moves.Move
import reactor.core.publisher.Mono

class OngoingGameService internal constructor(
    private val gameFetcherService: GameFetcherService,
    private val gameRepository: GameRepository,
    private val domainEventPublisher: EventPublisher<DomainEvent>
){
    fun nextRound(gameId: GameId): Mono<Unit> {
        return getOngoingGame(gameId)
            .map {
                it.nextRound()
            }
            .flatMap {
                persistAndPublishEvents(it)
                    .map { }
            }
    }

    fun registerMove(gameId: GameId, move: Move): Mono<Unit> {
        return getOngoingGame(gameId)
            .map {
                it.registerMove(move)
            }
            .flatMap {
                persistAndPublishEvents(it)
                    .map {  }
            }
    }

    private fun persistAndPublishEvents(game: Game): Mono<Game> {
        return gameRepository.save(game)
            .doOnSuccess {
                domainEventPublisher.publishEvents(game.recordedEvents)
            }
    }

    private fun getOngoingGame(gameId: GameId): Mono<OngoingGame> {
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
