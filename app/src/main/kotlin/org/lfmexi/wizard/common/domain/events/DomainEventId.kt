package org.lfmexi.wizard.common.domain.events

import java.util.UUID

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
