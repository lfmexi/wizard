package org.lfmexi.wizard.domain.moves

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.cards.ClassCard
import org.lfmexi.wizard.domain.exception.CardNotInHandException
import org.lfmexi.wizard.domain.exception.IllegalMoveException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.hands.Hand
import org.lfmexi.wizard.domain.rounds.PlayingPhaseRound
import org.lfmexi.wizard.domain.rounds.Round

data class Move(
    val id: MoveId,
    val hand: Hand,
    val card: Card,
    val round: PlayingPhaseRound
) {
    init {
        validate()
    }

    fun playCardFromHand(): Hand {
        return hand.playCard(card)
    }

    fun registerMoveOnRound(): Round {
        return round.registerPlayedCard(hand.playerId, card)
    }

    private fun validate() {
        validatePlayer()
        validateCardFromHand()
        validatePlayedCardGroup()
    }

    private fun validatePlayer() {
        if (hand.playerId != round.currentPlayer) {
            throw NotInTurnException(hand.playerId)
        }
    }

    private fun validateCardFromHand() {
        if (!hand.cards.contains(card)) {
            throw CardNotInHandException(hand, card)
        }
    }

    private fun validatePlayedCardGroup() {
        if (card !is ClassCard) {
            return
        }

        if (!round.hasPlayingCardGroup) {
            return
        }

        if (card.group == round.playingCardGroup) {
            return
        }

        val existingCard = hand.playCard(card)
            .cards
            .firstOrNull { it.group == round.playingCardGroup }

        if (existingCard != null) {
            throw IllegalMoveException(
                "The card $card cannot be played, the current " +
                    "card group that must be played is ${round.playingCardGroup}."
            )
        }
    }

    companion object {
        fun createMove(
            hand: Hand,
            card: Card,
            round: PlayingPhaseRound
        ): Move {
            return Move(
                id = MoveId.generate(),
                hand = hand,
                card = card,
                round = round
            )
        }
    }
}
