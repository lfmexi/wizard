package org.lfmexi.wizard.application.exceptions

import org.lfmexi.wizard.domain.rounds.RoundId

class DeclarationPhaseRoundNotFoundException(id: RoundId): RuntimeException(
    "No declaration phase round identified with $id has been found"
)
