package org.lfmexi.wizard.application.exceptions

import org.lfmexi.wizard.domain.rounds.RoundId

class DealingPhaseRoundNotFoundException(id: RoundId): RuntimeException(
    "No dealing phase round identified with $id has been found"
)
