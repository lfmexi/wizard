package org.lfmexi.wizard.application.support

import org.lfmexi.wizard.domain.DomainEvent
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
