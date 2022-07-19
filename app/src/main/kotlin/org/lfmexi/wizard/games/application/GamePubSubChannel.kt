package org.lfmexi.wizard.games.application

import org.lfmexi.wizard.common.domain.events.DomainEvent
import org.lfmexi.wizard.games.domain.GameId
import reactor.core.publisher.Flux

interface GamePubSubChannel {
    fun subscribeById(id: GameId): Flux<DomainEvent>
}
