package org.lfmexi.wizard.domain.values

data class NumericValue (
    val value: Int
) : Comparable<NumericValue> {
    override fun compareTo(other: NumericValue): Int {
        return value.compareTo(other.value)
    }

    operator fun plus(another: NumericValue) = NumericValue(this.value + another.value)

    companion object {
        val ONE = NumericValue(1)
        val ZERO = NumericValue(0)
        val MAX_NUMERIC_VALUE = NumericValue(Int.MAX_VALUE)
        val MIN_NUMERIC_VALUE = NumericValue(Int.MIN_VALUE)
    }
}
