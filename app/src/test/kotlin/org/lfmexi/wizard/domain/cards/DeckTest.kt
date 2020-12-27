package org.lfmexi.wizard.domain.cards

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.lfmexi.wizard.domain.values.NumericValue

internal class DeckTest {
    @Test
    fun `should draw the expected cards and subtract them properly from the deck`() {
        val deck = Deck.initialize()

        val (newDeck, drawnCards) = deck.drawCards(NumericValue(20))

        assertThat(newDeck.size).isEqualTo(NumericValue(40))
        assertThat(drawnCards).hasSize(20)
    }
}
