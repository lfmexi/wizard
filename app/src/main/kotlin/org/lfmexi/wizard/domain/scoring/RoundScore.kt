package org.lfmexi.wizard.domain.scoring

import org.lfmexi.wizard.domain.values.NumericValue

data class RoundScore(
    val expected: NumericValue,
    val actual: NumericValue
) {
    val totalScore: NumericValue get() {
        return if (expected == actual) {
            NumericValue(20) + expected * NumericValue(10)
        } else {
            (expected absoluteDiff actual) * NumericValue(-10)
        }
    }

    companion object {
        val ZERO_SCORE = RoundScore(NumericValue(0), NumericValue(0))
    }
}
