package org.lfmexi.wizard.application.exceptions

import org.lfmexi.wizard.domain.games.GameId

class StartedGameNotFoundException(id: GameId): RuntimeException(
    "No started game with id $id has been found"
)
