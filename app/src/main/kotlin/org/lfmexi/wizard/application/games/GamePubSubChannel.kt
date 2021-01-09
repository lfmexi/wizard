package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.games.GameId
import reactor.core.publisher.Flux

interface GamePubSubChannel {
    fun subscribeById(id: GameId): Flux<DomainEvent>
}
