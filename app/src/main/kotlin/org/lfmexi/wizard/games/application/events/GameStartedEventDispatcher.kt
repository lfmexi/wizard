package org.lfmexi.wizard.games.application.events

import mu.KotlinLogging
import org.lfmexi.wizard.games.application.OngoingGameService
import org.lfmexi.wizard.games.domain.GameStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
internal class GameStartedEventDispatcher(
    private val ongoingGameService: OngoingGameService
) {
    private val log = KotlinLogging.logger {  }

    @EventListener
    fun onGameStarted(event: GameStartedEvent) {
        with(event) {
            ongoingGameService.nextRound(game.id)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    log.info { "Next round triggered for ${game.id}" }
                }
        }
    }
}
