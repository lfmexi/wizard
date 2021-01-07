package org.lfmexi.wizard.domain.exception

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.hands.Hand

internal class CardNotInHandException(hand: Hand, card: Card): ValidationException(
    "The card $card is not in the hand of ${hand.playerId}"
)
