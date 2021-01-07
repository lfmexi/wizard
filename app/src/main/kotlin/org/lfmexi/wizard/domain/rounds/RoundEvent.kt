package org.lfmexi.wizard.domain.rounds

import org.lfmexi.wizard.domain.hands.Hand

sealed class RoundEvent {
    abstract val round: Round
}

data class RoundCreatedEvent(
    override val round: DealingPhaseRound
): RoundEvent()

data class DeclarationPhaseReadyEvent(
    override val round: DeclarationPhaseRound,
    val hands: List<Hand>
): RoundEvent()

data class DeclarationDoneEvent(
    override val round: DeclarationPhaseRound
): RoundEvent()

data class PlayingPhaseReadyEvent(
    override val round: PlayingPhaseRound
): RoundEvent()

data class RoundEndedEvent(
    override val round: Round
): RoundEvent()
