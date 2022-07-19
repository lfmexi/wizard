package org.lfmexi.wizard.players.domain.moves

import java.util.UUID

data class MoveId (
    val value: String
) {
    override fun toString(): String {
        return value
    }

    companion object {
        fun generate(): MoveId {
            return MoveId(UUID.randomUUID().toString())
        }
    }
}

