package org.lfmexi.wizard.application.games

import org.lfmexi.wizard.application.support.EventPublisher
import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.games.Game
import org.lfmexi.wizard.domain.players.PlayerId
import reactor.core.publisher.Mono

class GameCreationService internal constructor(
    private val gameRepository: GameRepository,
    private val gameEventPublisher: EventPublisher<DomainEvent>
)  {
    /**
     * Creates a new [Game]. It will start as a Lobby Game, waiting for more players to join
     * @param owner, the [PlayerId] of the owner of the game
     */
    fun createNewGame(owner: PlayerId): Mono<Game> {
        val game = Game.createNewGame(owner)
        return gameRepository.save(game)
            .doOnSuccess {
                gameEventPublisher.publishEvents(it.recordedEvents)
            }
    }
}
