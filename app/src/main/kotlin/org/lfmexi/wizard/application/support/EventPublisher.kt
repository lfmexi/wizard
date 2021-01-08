package org.lfmexi.wizard.application.support

interface EventPublisher<T> {
    fun publishEvents(events: Collection<T>)
}
