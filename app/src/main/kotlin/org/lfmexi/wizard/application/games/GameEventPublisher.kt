package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.games.GameEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class GameEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventPublisher<GameEvent> {
    override fun publishEvents(events: Collection<GameEvent>) {
        events.forEach {
            applicationEventPublisher.publishEvent(it)
        }
    }
}
