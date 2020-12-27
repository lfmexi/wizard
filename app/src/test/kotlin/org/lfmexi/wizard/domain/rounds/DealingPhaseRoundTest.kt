package org.lfmexi.wizard.domain.rounds

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.lfmexi.wizard.domain.Fixtures.DEALING_PHASE_ROUND
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_1
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_3
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.values.NumericValue

internal class DealingPhaseRoundTest {
    @Test
    fun `should not allow dealing if the player is not in turn`() {
        val initialPlayerId = PLAYER_ID_3
        val dealer = PLAYER_ID_1
        val dealingPhaseRound = DEALING_PHASE_ROUND.copy(
            initialPlayer = initialPlayerId
        )

        assertThrows<NotInTurnException> {
            dealingPhaseRound.deal(dealer)
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dealCardsTest")
    @Suppress("unused")
    fun `should deal the cards properly for the given round`(
        description: String,
        round: DealingPhaseRound,
        expectedAmountOfCards: Int,
        shouldHaveReferenceCardGroup: Boolean
    ) {
        val newPhaseRound = round.deal(round.initialPlayer) as DeclarationPhaseRound

        assertThat(newPhaseRound.referenceCardGroup != null).isEqualTo(shouldHaveReferenceCardGroup)
        assertThat(newPhaseRound.recordedEvents).hasSize(1)

        val event = newPhaseRound.recordedEvents.first() as DeclarationPhaseReadyEvent

        assertThat(event.hands).hasSize(newPhaseRound.players.size)
        event.hands.forEach {
            assertThat(it.cards).hasSize(expectedAmountOfCards)
        }
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun dealCardsTest(): List<Arguments> = listOf(
            Arguments.of(
                "First round, only one card per hand. Should have a non nullable reference card group",
                DEALING_PHASE_ROUND.copy(
                    roundNumber = NumericValue.ONE
                ),
                1,
                true
            ),
            Arguments.of(
                "Last round for 3 players, 20 cards each. Should not have a reference card group",
                DEALING_PHASE_ROUND.copy(
                    roundNumber = NumericValue(20)
                ),
                20,
                false
            )
        )
    }
}
