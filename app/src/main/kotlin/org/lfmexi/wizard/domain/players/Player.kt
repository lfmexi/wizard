package org.lfmexi.wizard.domain.players

data class Player (
    val id: PlayerId,
    val name: PlayerName
)

data class PlayerName (
    val value: String
) {
    override fun toString(): String {
        return value
    }
}
