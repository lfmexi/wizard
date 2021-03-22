package org.lfmexi.wizard.domain.players.moves

sealed class MoveEvent {
    abstract val move: PlayerMove
}
