package org.lfmexi.wizard.games.application.exceptions

import org.lfmexi.wizard.games.domain.rounds.RoundId

class PlayingPhaseRoundNotFoundException(id: RoundId): RuntimeException(
    "No playing phase round identified with $id has been found"
)
