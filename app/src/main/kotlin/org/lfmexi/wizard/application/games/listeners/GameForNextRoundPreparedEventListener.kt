package org.lfmexi.wizard.application.games.listeners

import mu.KotlinLogging
import org.lfmexi.wizard.application.rounds.RoundCreationService
import org.lfmexi.wizard.domain.games.GameForNextRoundPreparedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
internal class GameForNextRoundPreparedEventListener(
    private val roundCreationService: RoundCreationService
) {
    private val log = KotlinLogging.logger {  }
    @EventListener
    fun onNextRoundCreatedForGame(event: GameForNextRoundPreparedEvent) {
        with(event) {
            roundCreationService.createRound(game.id)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    log.info { "Round number ${it.roundNumber} for the game ${game.id} has been created" }
                }
        }
    }
}
