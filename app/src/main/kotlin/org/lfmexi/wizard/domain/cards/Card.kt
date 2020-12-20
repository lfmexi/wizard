package org.lfmexi.wizard.domain.cards

import org.lfmexi.wizard.domain.values.NumericValue

sealed class Card {
    abstract val group: CardGroup
    abstract val numericValue: NumericValue

    abstract fun canBeat(otherCard: Card, referenceGroup: CardGroup?): Boolean
}

data class ClassCard (
    override val group: CardGroup,
    override val numericValue: NumericValue
) : Card() {
    override fun canBeat(otherCard: Card, referenceGroup: CardGroup?): Boolean {
        return when (otherCard) {
            is WizardCard -> false
            is FoolCard -> true
            is ClassCard -> resolve(otherCard, referenceGroup)
        }
    }

    private fun resolve(otherCard: ClassCard, referenceGroup: CardGroup?): Boolean {
        return if (group == referenceGroup) {
            group != otherCard.group || numericValue > otherCard.numericValue
        } else {
            group == otherCard.group && numericValue > otherCard.numericValue
        }
    }
}

data class WizardCard (
    override val group: CardGroup
) : Card() {
    override val numericValue: NumericValue = NumericValue.MAX_NUMERIC_VALUE

    override fun canBeat(otherCard: Card, referenceGroup: CardGroup?): Boolean {
        return when (otherCard) {
            is WizardCard -> false
            else -> true
        }
    }
}

data class FoolCard (
    override val group: CardGroup
) : Card() {
    override val numericValue: NumericValue = NumericValue.MIN_NUMERIC_VALUE

    override fun canBeat(otherCard: Card, referenceGroup: CardGroup?): Boolean {
        return false
    }
}
