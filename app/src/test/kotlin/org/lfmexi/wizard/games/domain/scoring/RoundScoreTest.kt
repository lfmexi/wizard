package org.lfmexi.wizard.games.domain.scoring

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.lfmexi.wizard.common.domain.values.NumericValue
import org.lfmexi.wizard.games.domain.rounds.RoundScore

internal class RoundScoreTest {
    @ParameterizedTest
    @MethodSource("scores")
    fun `should calculate the total score of the round`(
        roundScore: RoundScore,
        expectedTotal: NumericValue
    ) {
       assertThat(roundScore.totalScore).isEqualTo(expectedTotal)
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        fun scores(): List<Arguments> = listOf(
            Arguments.of(
                RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                NumericValue(20)
            ),
            Arguments.of(
                RoundScore(NumericValue.ONE, NumericValue.ONE),
                NumericValue(30)
            ),
            Arguments.of(
                RoundScore(NumericValue.ZERO, NumericValue.ONE),
                NumericValue(-10)
            ),
            Arguments.of(
                RoundScore(NumericValue.ONE, NumericValue.ZERO),
                NumericValue(-10)
            )
        )
    }
}
