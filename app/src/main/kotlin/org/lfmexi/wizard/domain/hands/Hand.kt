package org.lfmexi.wizard.domain.hands

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.rounds.RoundId

data class Hand (
    val id: HandId,
    val roundId: RoundId,
    val playerId: PlayerId,
    val cards: List<Card>,
    val recordedEvents: List<HandEvent> = emptyList()
) {
    fun playCard(card: Card): Hand {
        val hand = this.copy(
            cards = cards - card
        )

        return hand.copy(
            recordedEvents = hand.recordedEvents + HandUpdatedEvent(hand)
        )
    }

    companion object {
        fun createHand(roundId: RoundId, playerId: PlayerId, cards: List<Card>): Hand {
            val hand = Hand(
                id = HandId.generate(),
                roundId = roundId,
                playerId = playerId,
                cards = cards
            )

            return hand.copy(recordedEvents = listOf(HandCreatedEvent(hand)))
        }
    }
}
