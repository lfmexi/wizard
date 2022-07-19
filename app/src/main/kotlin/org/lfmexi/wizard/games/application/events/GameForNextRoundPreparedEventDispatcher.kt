package org.lfmexi.wizard.games.application.events

import mu.KotlinLogging
import org.lfmexi.wizard.games.domain.NextRoundGeneratedForGameEvent
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
