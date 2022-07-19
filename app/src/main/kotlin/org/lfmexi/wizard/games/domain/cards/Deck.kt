package org.lfmexi.wizard.games.domain.cards

import org.lfmexi.wizard.common.domain.values.NumericValue

data class Deck internal constructor(
    val cards: List<Card>
) {
    val size = NumericValue(cards.size)

    fun drawCards(numberOfCards: NumericValue): Pair<Deck, List<Card>> {
        require(numberOfCards.value <= cards.size)

        val cardsWithRemaining = generateSequence({
            cards.subtractCardsFromPile()
        }) {
            (_, currentRemaining) ->
            currentRemaining.subtractCardsFromPile()
        }
            .take(numberOfCards.value)
            .toList()

        val (_, remainingCards) = cardsWithRemaining.last()
        val drawnCards = cardsWithRemaining.map { (card, _) -> card }

        val newDeck = this.copy(cards = remainingCards)

        return newDeck to drawnCards.toList()
    }

    fun drawTopCard(): Card? {
        return cards.firstOrNull()
    }

    private fun List<Card>.subtractCardsFromPile(): Pair<Card, List<Card>> {
        val card = this.random()
        return card to this - card
    }

    companion object {
        fun initialize(): Deck {
            val cards = CardGroup.values()
                .map {
                    generateClassCards(it) + FoolCard(it) + WizardCard(it)
                }.flatten()

            return Deck(
                cards = cards
            )
        }

        private fun generateClassCards(cardGroup: CardGroup): List<Card> {
            return generateSequence(NumericValue.ONE) { it + NumericValue.ONE }
                .take(13)
                .map {
                    ClassCard(cardGroup, it)
                }
                .toList()
        }
    }
}
