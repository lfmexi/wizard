package org.lfmexi.wizard.games.application.exceptions

import org.lfmexi.wizard.games.domain.rounds.RoundId

class DeclarationPhaseRoundNotFoundException(id: RoundId): RuntimeException(
    "No declaration phase round identified with $id has been found"
)
