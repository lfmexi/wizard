package org.lfmexi.wizard.domain.rounds

import org.junit.jupiter.params.provider.Arguments
import org.lfmexi.wizard.domain.Fixtures.DEALING_PHASE_ROUND
import org.lfmexi.wizard.domain.values.NumericValue

internal class DealingPhaseRoundTest {
//    @Test
//    fun `should not allow dealing if the player is not in turn`() {
//        val initialPlayerId = PLAYER_ID_3
//        val dealer = PLAYER_ID_1
//        val dealingPhaseRound = DEALING_PHASE_ROUND.copy(
//            dealingPlayer = initialPlayerId
//        )
//
//        assertThrows<NotInTurnException> {
//            dealingPhaseRound.deal(dealer)
//        }
//    }

//    @ParameterizedTest(name = "{0}")
//    @MethodSource("dealCardsTest")
//    @Suppress("unused")
//    fun `should deal the cards properly for the given round`(
//        description: String,
//        round: DealingPhaseRound,
//        expectedAmountOfCards: Int,
//        shouldHaveReferenceCardGroup: Boolean
//    ) {
//        val newPhaseRound = round.deal(round.dealingPlayer) as DeclarationPhaseRound
//
//        assertThat(newPhaseRound.referenceCardGroup != null).isEqualTo(shouldHaveReferenceCardGroup)
//        assertThat(newPhaseRound.recordedEvents).hasSize(1)
//        assertThat(newPhaseRound.recordedEvents.first()).isInstanceOf(DeclarationPhaseReadyEvent::class.java)
//    }

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
