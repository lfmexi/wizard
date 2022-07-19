package org.lfmexi.wizard.games.domain.exceptions

import org.lfmexi.wizard.common.domain.exceptions.ValidationException
import org.lfmexi.wizard.players.domain.PlayerId

internal class NotInTurnException(playerId: PlayerId) : ValidationException(
    "Player $playerId not in turn"
)