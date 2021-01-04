package org.lfmexi.wizard.domain.players

import java.util.UUID

data class PlayerId (
    val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): PlayerId {
            return PlayerId(UUID.randomUUID().toString())
        }
    }
}
