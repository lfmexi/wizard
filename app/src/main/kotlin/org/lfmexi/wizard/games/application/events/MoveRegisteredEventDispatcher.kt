package org.lfmexi.wizard.games.application.events

import mu.KotlinLogging
import org.lfmexi.wizard.games.domain.MoveRegisteredEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class MoveRegisteredEventDispatcher {
    private val log = KotlinLogging.logger {  }

    @EventListener
    fun onMoveRegistered(event: MoveRegisteredEvent) {
        log.info { "Notify the players about the move" }
    }
}
