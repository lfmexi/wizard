package org.lfmexi.wizard.domain.rounds

import java.util.UUID

data class RoundId(
    val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): RoundId {
            return RoundId(UUID.randomUUID().toString())
        }
    }
}