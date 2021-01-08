package org.lfmexi.wizard.application.rounds

import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.rounds.RoundEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class RoundEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventPublisher<RoundEvent> {
    override fun publishEvents(events: Collection<RoundEvent>) {
        events.forEach {
            applicationEventPublisher.publishEvent(it)
        }
    }
}
