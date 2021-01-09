package org.lfmexi.wizard.application.games.events

import mu.KotlinLogging
import org.lfmexi.wizard.domain.games.NextRoundGeneratedForGameEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class GameForNextRoundPreparedEventDispatcher{
    private val log = KotlinLogging.logger {  }
    @EventListener
    fun onNextRoundCreatedForGame(event: NextRoundGeneratedForGameEvent) {
        log.info { "Game ${event.game.id} prepared for next round" }
    }
}
