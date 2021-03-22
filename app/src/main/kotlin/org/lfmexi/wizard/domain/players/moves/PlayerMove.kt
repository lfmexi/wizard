package org.lfmexi.wizard.domain.players.moves

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.cards.Deck
import org.lfmexi.wizard.domain.hands.Hand
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.games.rounds.Round
import org.lfmexi.wizard.domain.values.NumericValue

sealed class PlayerMove {
    abstract val id: MoveId
    abstract val playerId: PlayerId
}

data class DealCardsPlayerMove(
    override val id: MoveId,
    override val playerId: PlayerId,
): PlayerMove() {
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
        fun create(dealer: PlayerId): DealCardsPlayerMove {
            return DealCardsPlayerMove(MoveId.generate(), dealer)
        }
    }
}

data class DeclarationPlayerMove(
    override val id: MoveId,
    override val playerId: PlayerId,
    val triumphsDeclared: NumericValue
): PlayerMove() {
    companion object {
        fun create(playerId: PlayerId, triumphsDeclared: NumericValue): DeclarationPlayerMove {
            return DeclarationPlayerMove(
                id = MoveId.generate(),
                playerId = playerId,
                triumphsDeclared = triumphsDeclared
            )
        }
    }
}

data class PlayCardPlayerMove(
    override val id: MoveId,
    override val playerId: PlayerId,
    val card: Card
): PlayerMove() {

    companion object {
        fun create(
            card: Card,
            playerId: PlayerId
        ): PlayCardPlayerMove {
            return PlayCardPlayerMove(
                id = MoveId.generate(),
                card = card,
                playerId = playerId
            )
        }
    }
}
