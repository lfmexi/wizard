package org.lfmexi.wizard.games.application.events

import mu.KotlinLogging
import org.lfmexi.wizard.games.domain.PlayerAddedToGameEvent
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
