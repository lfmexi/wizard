package org.lfmexi.wizard.games.application

import org.lfmexi.wizard.games.domain.Game
import org.lfmexi.wizard.games.domain.GameId
import org.lfmexi.wizard.players.domain.PlayerId
import reactor.core.publisher.Mono

class LobbyGameService internal constructor(
    private val gameFetcherService: GameFetcherService,
    private val gamePersistenceService: GamePersistenceService
)  {
    /**
     * Adds a player to the given game
     * @param playerId the [PlayerId] to be added to the game
     * @param gameId the [GameId] identifying the game
     * @return a [Mono] containing the new [Game]
     */
    fun addPlayerToTheGame(playerId: PlayerId, gameId: GameId): Mono<Game> {
        return gameFetcherService.getGame(gameId)
            .map {
                it.addPlayer(playerId)
            }
            .flatMap {
                gamePersistenceService.persistAndPublishEvents(it)
            }
    }

    /**
     * Starts the given game setting the [PlayerId] as initiator
     * @param playerId
     * @param gameId
     * @return a [Mono] containing the new ongoing [Game]
     */
    fun startGame(playerId: PlayerId, gameId: GameId): Mono<Game> {
        return gameFetcherService.getGame(gameId)
            .map {
                it.startGame(playerId)
            }
            .flatMap {
                gamePersistenceService.persistAndPublishEvents(it)
            }
    }
}
