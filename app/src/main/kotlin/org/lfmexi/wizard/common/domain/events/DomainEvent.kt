package org.lfmexi.wizard.common.domain.events

import java.time.Instant

abstract class DomainEvent(
    val id: DomainEventId,
    val timestamp: Instant
)
