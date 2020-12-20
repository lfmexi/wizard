package org.lfmexi.wizard.domain.cards

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.lfmexi.wizard.domain.values.NumericValue

internal class CardTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("allTypes")
    fun `should lose against Wizard`(card: Card) {
        // given
        // a wizard card with an arbitrary card group
        val wizardCard = WizardCard(CardGroup.RED)

        // when - then (even with the same card group)
        assertThat(card.canBeat(wizardCard, card.group))
                .isFalse()
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allButFool")
    fun `should win against Fool`(card: Card) {
        // given
        // a fool card with an arbitrary card group
        val foolCard = FoolCard(CardGroup.RED)

        // when - then (even with the same card group)
        assertThat(card.canBeat(foolCard, card.group))
                .isTrue()
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("commonCards")
    fun `should check if the card can beat the given one`(
        card: Card,
        cardToCompare: Card,
        referenceGroup: CardGroup,
        expectedValue: Boolean
    ) {
        // given
        // when - then
        assertThat(card.canBeat(cardToCompare, referenceGroup))
                .isEqualTo(expectedValue)
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun allTypes(): List<Arguments> =
            listOf(
                Arguments.of(
                    ClassCard(CardGroup.RED, NumericValue(13))
                ),
                Arguments.of(
                    WizardCard(CardGroup.RED)
                ),
                Arguments.of(
                    FoolCard(CardGroup.RED)
                )
            )

        @JvmStatic
        @Suppress("unused")
        fun allButFool(): List<Arguments> =
            listOf(
                Arguments.of(
                    ClassCard(CardGroup.RED, NumericValue(13))
                ),
                Arguments.of(
                    WizardCard(CardGroup.RED)
                )
            )

        @JvmStatic
        @Suppress("unused")
        fun commonCards(): List<Arguments> =
            listOf(
                Arguments.of(
                    ClassCard(CardGroup.RED, NumericValue(13)),
                    ClassCard(CardGroup.RED, NumericValue(12)),
                    CardGroup.BLUE,
                    true
                ),
                Arguments.of(
                    ClassCard(CardGroup.RED, NumericValue(1)),
                    ClassCard(CardGroup.GREEN, NumericValue(12)),
                    CardGroup.RED,
                    true
                ),
                Arguments.of(
                    ClassCard(CardGroup.YELLOW, NumericValue(13)),
                    ClassCard(CardGroup.GREEN, NumericValue(1)),
                    CardGroup.RED,
                    false
                ),
                Arguments.of(
                    WizardCard(CardGroup.YELLOW),
                    ClassCard(CardGroup.GREEN, NumericValue(13)),
                    CardGroup.GREEN,
                    true
                )
            )
    }
}