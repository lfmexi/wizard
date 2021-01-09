package org.lfmexi.wizard.domain.events

import java.time.Instant
import java.util.UUID

abstract class DomainEvent(
    val id: DomainEventId,
    val timestamp: Instant
)

data class DomainEventId(
    val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): DomainEventId {
            return DomainEventId(UUID.randomUUID().toString())
        }
    }
}
