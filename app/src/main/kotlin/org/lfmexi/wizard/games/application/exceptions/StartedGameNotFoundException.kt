package org.lfmexi.wizard.games.application.exceptions

import org.lfmexi.wizard.games.domain.GameId

class StartedGameNotFoundException(id: GameId): RuntimeException(
    "No started game with id $id has been found"
)
