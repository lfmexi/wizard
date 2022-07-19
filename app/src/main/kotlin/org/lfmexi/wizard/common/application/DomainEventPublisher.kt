package org.lfmexi.wizard.common.application

import org.lfmexi.wizard.common.domain.events.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class DomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventPublisher<DomainEvent> {
    override fun publishEvents(events: Collection<DomainEvent>) {
        events.forEach {
            applicationEventPublisher.publishEvent(it)
        }
    }
}
