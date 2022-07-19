package org.lfmexi.wizard.games.domain.exceptions

import org.lfmexi.wizard.common.domain.exceptions.ValidationException

internal class IllegalMoveException(message: String) : ValidationException(message)