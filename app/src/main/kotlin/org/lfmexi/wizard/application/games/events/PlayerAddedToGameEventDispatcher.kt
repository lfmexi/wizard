package org.lfmexi.wizard.application.games.events

import mu.KotlinLogging
import org.lfmexi.wizard.domain.games.PlayerAddedToGameEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class PlayerAddedToGameEventDispatcher {
    private val log = KotlinLogging.logger {  }

    @EventListener
    fun onPlayerAddedToGame(event: PlayerAddedToGameEvent) {
        log.info { "Player added to game ${event.game.id}. Current players are ${event.game.players}" }
    }
}
