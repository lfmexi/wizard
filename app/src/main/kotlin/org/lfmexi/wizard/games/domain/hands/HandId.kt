package org.lfmexi.wizard.games.domain.hands

import java.util.UUID

data class HandId(
    val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): HandId {
            return HandId(UUID.randomUUID().toString())
        }
    }
}
