package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.games.Game
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
