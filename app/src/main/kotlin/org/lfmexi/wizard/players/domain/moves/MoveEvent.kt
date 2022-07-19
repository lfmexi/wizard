package org.lfmexi.wizard.players.domain.moves

sealed class MoveEvent {
    abstract val move: PlayerMove
}
