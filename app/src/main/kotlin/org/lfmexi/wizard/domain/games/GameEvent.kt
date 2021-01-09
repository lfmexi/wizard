package org.lfmexi.wizard.domain.games

import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.events.DomainEventId
import java.time.Instant

sealed class GameEvent : DomainEvent(
    id = DomainEventId.generate(),
    timestamp = Instant.now()
) {
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

data class NextRoundGeneratedForGameEvent(
    override val game: Game
): GameEvent()

data class MoveRegisteredEvent(
    override val game: Game
): GameEvent()

data class GameEndedEvent(
    override val game: Game
): GameEvent()
