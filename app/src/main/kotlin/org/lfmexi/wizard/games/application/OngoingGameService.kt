package org.lfmexi.wizard.games.application

import org.lfmexi.wizard.games.application.exceptions.OngoingGameNotFoundException
import org.lfmexi.wizard.games.domain.Game
import org.lfmexi.wizard.games.domain.GameId
import org.lfmexi.wizard.games.domain.OngoingGame
import org.lfmexi.wizard.players.domain.moves.PlayerMove
import reactor.core.publisher.Mono

class OngoingGameService internal constructor(
    private val gameFetcherService: GameFetcherService,
    private val gamePersistenceService: GamePersistenceService
){
    fun nextRound(gameId: GameId): Mono<Game> {
        return getOngoingGame(gameId)
            .map {
                it.nextRound()
            }
            .flatMap {
                gamePersistenceService.persistAndPublishEvents(it)
            }
    }

    fun registerMove(gameId: GameId, move: PlayerMove): Mono<Game> {
        return getOngoingGame(gameId)
            .map {
                it.registerMove(move)
            }
            .flatMap {
                gamePersistenceService.persistAndPublishEvents(it)
            }
    }

    private fun getOngoingGame(gameId: GameId): Mono<OngoingGame> {
        return gameFetcherService.getGame(gameId)
            .flatMap {
                if (it is OngoingGame) {
                    Mono.just(it)
                } else {
                    Mono.error(OngoingGameNotFoundException("Game $gameId cannot be found as an ongoing game"))
                }
            }
    }
}
