package org.lfmexi.wizard.domain.rounds

import org.lfmexi.wizard.domain.players.Hand

sealed class RoundEvent {
    abstract val round: Round
}

data class RoundCreatedEvent(
    override val round: Round
): RoundEvent()

data class DeclarationPhaseReadyEvent(
    override val round: Round
): RoundEvent()

data class MoveInRoundRegisteredEvent(
    override val round: Round,
    val hand: Hand
): RoundEvent()

data class PlayingPhaseReadyEvent(
    override val round: Round
): RoundEvent()

data class RoundEndedEvent(
    override val round: Round
): RoundEvent()