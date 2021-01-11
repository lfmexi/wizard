package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.players.PlayerId
import reactor.core.publisher.Mono

class GameCreationService internal constructor(
    private val gamePersistenceService: GamePersistenceService
)  {
    /**
     * Creates a new [Game]. It will start as a Lobby Game, waiting for more players to join
     * @param owner, the [PlayerId] of the owner of the game
     */
    fun createNewGame(owner: PlayerId): Mono<Game> {
        return gamePersistenceService.persistAndPublishEvents(Game.createNewGame(owner))
    }
}
