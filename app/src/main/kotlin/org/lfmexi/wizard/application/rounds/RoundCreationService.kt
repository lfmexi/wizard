package org.lfmexi.wizard.application.rounds

import org.lfmexi.wizard.application.exceptions.OngoingGameNotFoundException
import org.lfmexi.wizard.application.games.GameFetcherService
import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.OngoingGame
import org.lfmexi.wizard.domain.rounds.Round
import org.lfmexi.wizard.domain.rounds.RoundEvent
import reactor.core.publisher.Mono

class RoundCreationService internal constructor(
    private val gameFetcherService: GameFetcherService,
    private val roundRepository: RoundRepository,
    private val roundEventPublisher: EventPublisher<RoundEvent>
) {
    /**
     * Creates a new round for the given [GameId]. It must be an [OngoingGame]
     * @param gameId
     * @return a [Mono] containing the newly created [Round]
     */
    fun createRound(gameId: GameId): Mono<Round> {
        return getOngoingGame(gameId)
            .map {
                Round.createNewRound(it)
            }
            .flatMap {
                roundRepository.save(it)
            }
            .doOnSuccess {
                roundEventPublisher.publishEvents(it.recordedEvents)
            }
    }

    private fun getOngoingGame(gameId: GameId): Mono<OngoingGame> {
        return gameFetcherService.getGame(gameId)
            .flatMap {
                if (it is OngoingGame) {
                    Mono.just(it)
                } else {
                    Mono.error(OngoingGameNotFoundException("Ongoing game not found for game id $gameId"))
                }
            }
    }
}
