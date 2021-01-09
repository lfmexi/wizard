package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.players.PlayerId
import reactor.core.publisher.Mono

class LobbyGameService internal constructor(
    private val gameFetcherService: GameFetcherService,
    private val gameRepository: GameRepository,
    private val gameEventPublisher: EventPublisher<DomainEvent>
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
            .persistAndPublishEvents()
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
            .persistAndPublishEvents()
    }

    private fun Mono<Game>.persistAndPublishEvents(): Mono<Game> {
        return this
            .flatMap {
                gameRepository.save(it)
            }
            .doOnSuccess {
                gameEventPublisher.publishEvents(it.recordedEvents)
            }
    }
}
