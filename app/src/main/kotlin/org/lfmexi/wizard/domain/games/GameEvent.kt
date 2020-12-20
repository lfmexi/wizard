package org.lfmexi.wizard.domain.games

import org.lfmexi.wizard.domain.rounds.Round

sealed class GameEvent {
    abstract val game: Game
}

data class GameCreatedEvent(
    override val game: Game
): GameEvent()

data class PlayerAddedEvent (
    override val game: Game
) : GameEvent()

data class GameStartedEvent (
    override val game: Game
) : GameEvent()

data class NextRoundCreatedForGameEvent(
    override val game: Game,
    val round: Round
): GameEvent()

data class GameEndedEvent(
    override val game: Game
): GameEvent()