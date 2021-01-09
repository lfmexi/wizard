package org.lfmexi.wizard.domain.rounds

import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.events.DomainEventId
import java.time.Instant

sealed class RoundEvent: DomainEvent(
    id = DomainEventId.generate(),
    timestamp = Instant.now()
) {
    abstract val round: Round
}

data class RoundCreatedEvent(
    override val round: DealingPhaseRound
): RoundEvent()

data class DeclarationPhaseReadyEvent(
    override val round: DeclarationPhaseRound
): RoundEvent()

data class DeclarationDoneEvent(
    override val round: DeclarationPhaseRound
): RoundEvent()

data class PlayingPhaseReadyEvent(
    override val round: PlayingPhaseRound
): RoundEvent()

data class TriumphEndedEvent(
    override val round: PlayingPhaseRound
): RoundEvent()

data class RoundEndedEvent(
    override val round: Round
): RoundEvent()
