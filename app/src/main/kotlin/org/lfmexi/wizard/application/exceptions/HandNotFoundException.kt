package org.lfmexi.wizard.application.exceptions

import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.games.rounds.RoundId

class HandNotFoundException(playerId: PlayerId, roundId: RoundId): RuntimeException(
    "Hand not found for player $playerId in round $roundId "
)
