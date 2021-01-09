package org.lfmexi.wizard.domain.moves

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.cards.ClassCard
import org.lfmexi.wizard.domain.cards.Deck
import org.lfmexi.wizard.domain.exception.CardNotInHandException
import org.lfmexi.wizard.domain.exception.ExpectedScoreNotAcceptedException
import org.lfmexi.wizard.domain.exception.IllegalMoveException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.hands.Hand
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.rounds.DealingPhaseRound
import org.lfmexi.wizard.domain.rounds.DeclarationPhaseRound
import org.lfmexi.wizard.domain.rounds.PlayingPhaseRound
import org.lfmexi.wizard.domain.rounds.Round
import org.lfmexi.wizard.domain.values.NumericValue

sealed class Move {
    abstract val id: MoveId
    abstract val playerId: PlayerId
}

data class DealCardsMove(
    override val id: MoveId,
    override val playerId: PlayerId,
): Move() {
    private val deck: Deck = Deck.initialize()

    internal fun deal(round: Round): Pair<List<Hand>, Card?> {
        val (deck, hands) = dealHands(round)
        return hands to deck.drawTopCard()
    }

    private fun dealHands(round: Round): Pair<Deck, List<Hand>> {
        val cardsWithDeck: List<Pair<Deck, List<Card>>> =
            generateSequence({ deck.drawCards(round.roundNumber) }) {
                (deck, _) -> deck.drawCards(round.roundNumber)
            }
                .take(round.players.size)
                .toList()

        val (lastDeck, _) = cardsWithDeck.last()
        val cardsForHands = cardsWithDeck.map { (_, cards) -> cards }

        return lastDeck to cardsForHands.mapIndexed { index, cards ->
            Hand.createHand(round.id, round.players[index], cards)
        }
    }

    companion object {
        fun create(dealer: PlayerId): DealCardsMove {
            return DealCardsMove(MoveId.generate(), dealer)
        }
    }
}

data class DeclarationMove(
    override val id: MoveId,
    override val playerId: PlayerId,
    val triumphsDeclared: NumericValue
): Move() {
    companion object {
        fun create(playerId: PlayerId, triumphsDeclared: NumericValue): DeclarationMove {
            return DeclarationMove(
                id = MoveId.generate(),
                playerId = playerId,
                triumphsDeclared = triumphsDeclared
            )
        }
    }
}

data class PlayCardMove(
    override val id: MoveId,
    override val playerId: PlayerId,
    val card: Card
): Move() {

    companion object {
        fun create(
            card: Card,
            playerId: PlayerId
        ): PlayCardMove {
            return PlayCardMove(
                id = MoveId.generate(),
                card = card,
                playerId = playerId
            )
        }
    }
}
