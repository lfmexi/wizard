package org.lfmexi.wizard.common.application

interface EventPublisher<T> {
    fun publishEvents(events: Collection<T>)
}
