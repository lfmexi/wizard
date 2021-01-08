package org.lfmexi.wizard.application.rounds.listeners

import mu.KotlinLogging
import org.lfmexi.wizard.domain.rounds.RoundCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class RoundCreatedEventListener {
    private val log = KotlinLogging.logger { }
    @EventListener
    fun onRoundCreated(event: RoundCreatedEvent) {
        log.info { "round created ${event.round.id} synchronize the players" }
    }
}
