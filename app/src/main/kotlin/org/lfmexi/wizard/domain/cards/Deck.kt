package org.lfmexi.wizard.domain.cards

import org.lfmexi.wizard.domain.values.NumericValue

data class Deck internal constructor(
    val cards: List<Card>
) {
    val size = NumericValue(cards.size)

    fun drawCards(numberOfCards: NumericValue): Pair<Deck, List<Card>> {
        require(numberOfCards.value <= cards.size)

        val drawnCards = generateSequence { cards.random() }
            .take(numberOfCards.value)
            .toList()

        val newDeck = this.copy(cards = cards - drawnCards)

        return newDeck to drawnCards
    }

    fun drawTopCard(): Card? {
        return cards.firstOrNull()
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
