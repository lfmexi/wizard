package org.lfmexi.wizard.application.exceptions

import org.lfmexi.wizard.domain.rounds.RoundId

class PlayingPhaseRoundNotFoundException(id: RoundId): RuntimeException(
    "No playing phase round identified with $id has been found"
)
