package org.lfmexi.wizard.application.games.listeners

import mu.KotlinLogging
import org.lfmexi.wizard.domain.games.GameCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class GameCreatedEventListener {
    private val log = KotlinLogging.logger {  }

    @EventListener
    fun onGameCreated(event: GameCreatedEvent) {
        // just log for the moment
        log.info { "New game has been created ${event.game.id}" }
    }
}
