package org.lfmexi.wizard.domain.games

sealed class GameEvent {
    abstract val game: Game
}

data class GameCreatedEvent(
    override val game: Game
): GameEvent()

data class PlayerAddedToGameEvent (
    override val game: Game
) : GameEvent()

data class GameStartedEvent (
    override val game: Game
) : GameEvent()

data class GameForNextRoundPreparedEvent(
    override val game: Game
): GameEvent()

data class GameEndedEvent(
    override val game: Game
): GameEvent()
