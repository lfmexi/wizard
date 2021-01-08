package org.lfmexi.wizard.application.games.listeners

import mu.KotlinLogging
import org.lfmexi.wizard.application.games.OngoingGameService
import org.lfmexi.wizard.domain.games.EndedGame
import org.lfmexi.wizard.domain.games.GameStartedEvent
import org.lfmexi.wizard.domain.games.OngoingGame
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
internal class GameStartedEventListener(
    private val ongoingGameService: OngoingGameService
) {
    private val log = KotlinLogging.logger {  }

    @EventListener
    fun onGameStarted(event: GameStartedEvent) {
        with(event) {
            ongoingGameService.updateForNextRound(game.id)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    when(it) {
                        is OngoingGame -> log.info { "Game ${it.id} in round ${it.ongoingRound}" }
                        is EndedGame -> log.warn { "Game ${it.id} ended" }
                        else -> log.error { "Game ${it.id} is an non expected stated" }
                    }
                }
        }
    }
}
