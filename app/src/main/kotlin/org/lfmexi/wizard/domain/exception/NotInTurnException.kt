package org.lfmexi.wizard.domain.exception

import org.lfmexi.wizard.domain.players.PlayerId

internal class NotInTurnException(playerId: PlayerId) : ValidationException(
    "Player $playerId not in turn"
)