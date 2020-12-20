package org.lfmexi.wizard.domain.scoring

import org.lfmexi.wizard.domain.values.NumericValue

data class RoundScore(
    val expected: NumericValue,
    val actual: NumericValue
) {
    companion object {
        val ZERO_SCORE = RoundScore(NumericValue(0), NumericValue(0))
    }
}