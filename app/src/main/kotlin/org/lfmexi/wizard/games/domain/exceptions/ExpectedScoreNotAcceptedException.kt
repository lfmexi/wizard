package org.lfmexi.wizard.games.domain.exceptions

import org.lfmexi.wizard.common.domain.exceptions.ValidationException
import org.lfmexi.wizard.common.domain.values.NumericValue

internal class ExpectedScoreNotAcceptedException(
    expectedScore: NumericValue,
    constraint: NumericValue
) : ValidationException(
    "Expected score $expectedScore. Sum of scores cannot be equal to $constraint"
)