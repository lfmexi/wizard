package org.lfmexi.wizard.domain.players

import java.util.UUID

data class Player (
    val id: PlayerId,
    val name: PlayerName
)

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

data class PlayerName (
    val value: String
) {
    override fun toString(): String {
        return value
    }
}