package org.lfmexi.wizard.games.domain.exceptions

import org.lfmexi.wizard.common.domain.exceptions.ValidationException

internal class NotGameOwnerStartException(message: String) : ValidationException(message)