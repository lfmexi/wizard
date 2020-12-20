package org.lfmexi.wizard.domain.exception

import org.lfmexi.wizard.domain.values.NumericValue

internal class ExpectedScoreNotAcceptedException(
    expectedScore: NumericValue,
    constraint: NumericValue
) : ValidationException(
    "Expected score $expectedScore. Sum of scores cannot be equal to $constraint"
)