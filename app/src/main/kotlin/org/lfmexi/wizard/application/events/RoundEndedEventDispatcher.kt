package org.lfmexi.wizard.application.events

import mu.KotlinLogging
import org.lfmexi.wizard.application.games.OngoingGameService
import org.lfmexi.wizard.domain.rounds.RoundEndedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
internal class RoundEndedEventDispatcher(
    private val ongoingGameService: OngoingGameService
) {
    private val log = KotlinLogging.logger {  }

    @EventListener
    fun onRoundEnded(event: RoundEndedEvent) {
        with(event) {
            log.info { "Round ${round.id} of game ${round.gameId} ended" }

            ongoingGameService.nextRound(gameId = round.gameId)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    log.info { "Next round triggered for ${round.gameId}" }
                }
        }
    }
}
