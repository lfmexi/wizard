package org.lfmexi.wizard.games.application.exceptions

import org.lfmexi.wizard.games.domain.rounds.RoundId

class DealingPhaseRoundNotFoundException(id: RoundId): RuntimeException(
    "No dealing phase round identified with $id has been found"
)
