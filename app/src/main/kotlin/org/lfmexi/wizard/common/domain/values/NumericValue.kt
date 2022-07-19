package org.lfmexi.wizard.common.domain.values

import kotlin.math.abs

data class NumericValue (
    val value: Int
) : Comparable<NumericValue> {
    override fun compareTo(other: NumericValue): Int {
        return value.compareTo(other.value)
    }

    operator fun plus(another: NumericValue) = NumericValue(this.value + another.value)

    operator fun times(another: NumericValue) = NumericValue(this.value * another.value)

    infix fun absoluteDiff(another: NumericValue): NumericValue {
        return NumericValue(abs(this.value - another.value))
    }

    companion object {
        val ONE = NumericValue(1)
        val ZERO = NumericValue(0)
    }
}
