package org.lfmexi.wizard.domain.players

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.rounds.RoundId
import java.util.UUID

data class Hand (
    val id: HandId,
    val roundId: RoundId,
    val playerId: PlayerId,
    val cards: List<Card>
) {
    fun removeCard(card: Card): Hand {
        return this.copy(
            cards = cards - card
        )
    }

    companion object {
        fun createHand(roundId: RoundId, playerId: PlayerId, cards: List<Card>): Hand {
            return Hand(
                id = HandId.generate(),
                roundId = roundId,
                playerId = playerId,
                cards = cards
            )
        }
    }
}

data class HandId(
    val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): HandId {
            return HandId(UUID.randomUUID().toString())
        }
    }
}