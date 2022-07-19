package org.lfmexi.wizard.games.domain.exceptions

import org.lfmexi.wizard.common.domain.exceptions.ValidationException
import org.lfmexi.wizard.games.domain.cards.Card
import org.lfmexi.wizard.games.domain.hands.Hand

internal class CardNotInHandException(hand: Hand, card: Card): ValidationException(
    "The card $card is not in the hand of ${hand.playerId}"
)
