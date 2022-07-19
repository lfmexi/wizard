package org.lfmexi.wizard.games.domain.hands

import org.lfmexi.wizard.games.domain.cards.Card
import org.lfmexi.wizard.players.domain.PlayerId
import org.lfmexi.wizard.games.domain.rounds.RoundId

data class Hand (
    val id: HandId,
    val roundId: RoundId,
    val playerId: PlayerId,
    val cards: List<Card>
) {
    fun playCard(card: Card): Hand {
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
