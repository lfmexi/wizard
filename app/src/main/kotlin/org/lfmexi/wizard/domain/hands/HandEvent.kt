package org.lfmexi.wizard.domain.hands

sealed class HandEvent {
    abstract val hand: Hand
}

data class HandCreatedEvent(
    override val hand: Hand
): HandEvent()

data class HandUpdatedEvent(
    override val hand: Hand
): HandEvent()
