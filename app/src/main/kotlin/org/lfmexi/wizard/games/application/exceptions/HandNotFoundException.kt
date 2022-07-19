package org.lfmexi.wizard.games.application.exceptions

import org.lfmexi.wizard.players.domain.PlayerId
import org.lfmexi.wizard.games.domain.rounds.RoundId

class HandNotFoundException(playerId: PlayerId, roundId: RoundId): RuntimeException(
    "Hand not found for player $playerId in round $roundId "
)
