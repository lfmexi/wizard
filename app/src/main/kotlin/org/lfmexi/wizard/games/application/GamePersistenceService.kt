package org.lfmexi.wizard.games.application

import org.lfmexi.wizard.common.application.EventPublisher
import org.lfmexi.wizard.common.domain.events.DomainEvent
import org.lfmexi.wizard.games.domain.Game
import reactor.core.publisher.Mono

class GamePersistenceService internal constructor(
    private val gameRepository: GameRepository,
    private val domainEventPublisher: EventPublisher<DomainEvent>
) {
    fun persistAndPublishEvents(game: Game): Mono<Game> {
        return gameRepository.save(game)
            .doOnSuccess {
                domainEventPublisher.publishEvents(game.recordedEvents)
            }
    }
}
