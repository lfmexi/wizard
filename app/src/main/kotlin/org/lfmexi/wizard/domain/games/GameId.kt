package org.lfmexi.wizard.domain.games

import java.util.UUID

data class GameId (
        val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): GameId {
            return GameId(UUID.randomUUID().toString())
        }
    }
}