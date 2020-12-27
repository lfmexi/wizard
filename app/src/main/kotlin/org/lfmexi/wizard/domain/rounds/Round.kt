package org.lfmexi.wizard.domain.rounds

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.cards.CardGroup
import org.lfmexi.wizard.domain.cards.ClassCard
import org.lfmexi.wizard.domain.cards.Deck
import org.lfmexi.wizard.domain.cards.FoolCard
import org.lfmexi.wizard.domain.cards.WizardCard
import org.lfmexi.wizard.domain.exception.CardNotInHandException
import org.lfmexi.wizard.domain.exception.ExpectedScoreNotAcceptedException
import org.lfmexi.wizard.domain.exception.IllegalMoveException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.OngoingGame
import org.lfmexi.wizard.domain.players.Hand
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.scoring.RoundScore
import org.lfmexi.wizard.domain.values.NumericValue

sealed class Round {
    abstract val id: RoundId
    abstract val gameId: GameId
    abstract val roundNumber: NumericValue
    abstract val players: List<PlayerId>
    abstract val playerScoreBoard: Map<PlayerId, RoundScore>
    abstract val initialPlayer: PlayerId
    abstract val recordedEvents: List<RoundEvent>

    companion object {
        fun createNewRound(
            game: OngoingGame,
            initialPlayer: PlayerId
        ): Round {
            return DealingPhaseRound.createNewRound(
                game = game,
                initialPlayer = initialPlayer,
                players = game.players,
                deck = game.deck
            )
        }
    }
}

data class DealingPhaseRound(
    override val id: RoundId,
    override val gameId: GameId,
    override val roundNumber: NumericValue,
    override val players: List<PlayerId>,
    override val playerScoreBoard: Map<PlayerId, RoundScore>,
    override val initialPlayer: PlayerId,
    override val recordedEvents: List<RoundEvent> = emptyList(),
    private val deck: Deck
) : Round() {

    /**
     * Once a [Round] is created, a deal phase starts.
     * @return a [Pair] of [Round] and [List] of [Hand]
     */
    fun deal(dealer: PlayerId): Round {
        if (dealer != initialPlayer) {
            throw NotInTurnException(dealer)
        }

        val cardsWithDeck: List<Pair<Deck, List<Card>>> = generateSequence({ deck.drawCards(roundNumber) }) {
            (deck, _) -> deck.drawCards(roundNumber)
        }
            .take(players.size)
            .toList()

        val (lastDeck, _) = cardsWithDeck.last()
        val cardsForHands = cardsWithDeck.map { (_, cards) -> cards }

        val hands = cardsForHands.mapIndexed { index, cards ->
            Hand.createHand(id, players[index], cards)
        }

        val activeCard = lastDeck.drawTopCard()

        return DeclarationPhaseRound.from(this, hands, activeCard?.group)
    }

    internal companion object {
        fun createNewRound(
            game: OngoingGame,
            initialPlayer: PlayerId,
            players: List<PlayerId>,
            deck: Deck
        ): Round {
            val round = DealingPhaseRound(
                id = RoundId.generate(),
                gameId = game.id,
                roundNumber = game.ongoingRound,
                players = players,
                playerScoreBoard = players.map { it to RoundScore.ZERO_SCORE }.toMap(),
                initialPlayer = initialPlayer,
                deck = deck
            )

            return round.copy(
                recordedEvents = listOf(RoundCreatedEvent(round))
            )
        }
    }
}

sealed class PlayerTurnPhaseRound : Round() {
    abstract val currentPlayer: PlayerId

    protected fun nextPlayer(): PlayerId {
        val nextPlayerIndex = players.indexOf(currentPlayer) + 1

        return if (nextPlayerIndex >= players.size) {
            players.first()
        } else {
            players[nextPlayerIndex]
        }
    }
}

data class DeclarationPhaseRound(
    override val id: RoundId,
    override val gameId: GameId,
    override val roundNumber: NumericValue,
    override val players: List<PlayerId>,
    override val playerScoreBoard: Map<PlayerId, RoundScore>,
    override val initialPlayer: PlayerId,
    override val recordedEvents: List<RoundEvent> = emptyList(),
    override val currentPlayer: PlayerId,
    val referenceCardGroup: CardGroup?
): PlayerTurnPhaseRound() {
    /**
     * Every player must declare how many times they will win in this round. This will trigger
     * a new turn creation
     */
    fun declare(playerId: PlayerId, value: NumericValue): Round {
        validateDeclaration(playerId, value)

        val mutableScoreBoard = playerScoreBoard.toMutableMap()

        mutableScoreBoard[playerId] = mutableScoreBoard[playerId]!!.copy(
            expected = value
        )

        val declaredRound = this.copy(
            playerScoreBoard = mutableScoreBoard,
            currentPlayer = nextPlayer()
        )

        return if (declaredRound.currentPlayer == initialPlayer) {
            // end the round
            PlayingPhaseRound.from(declaredRound)
        } else {
            declaredRound.copy(
                recordedEvents = declaredRound.recordedEvents + DeclarationDoneEvent(declaredRound)
            )
        }
    }

    private fun validateDeclaration(playerId: PlayerId, value: NumericValue) {
        if (playerId != currentPlayer) {
            throw NotInTurnException(playerId)
        }

        if (isLastPlayer(playerId)) {
            val sumOfExpected = playerScoreBoard.map { (_, score) -> score.expected }
                .sumOf { it.value }
                .let { NumericValue(it) } + value

            if (sumOfExpected == roundNumber) throw ExpectedScoreNotAcceptedException(value, roundNumber)
        }
    }

    private fun isLastPlayer(playerId: PlayerId): Boolean {
        return playerId == players.last()
    }

    internal companion object {
        fun from(
            round: Round,
            hands: List<Hand>,
            referenceCardGroup: CardGroup?
        ): DeclarationPhaseRound {
            return with(round) {
                val nextRound = DeclarationPhaseRound(
                    id = id,
                    gameId = gameId,
                    roundNumber = roundNumber,
                    players = players,
                    playerScoreBoard = playerScoreBoard,
                    initialPlayer = initialPlayer,
                    recordedEvents = recordedEvents,
                    currentPlayer = initialPlayer,
                    referenceCardGroup = referenceCardGroup
                )
                nextRound.copy(
                    recordedEvents = nextRound.recordedEvents + DeclarationPhaseReadyEvent(nextRound, hands)
                )
            }
        }
    }
}

data class PlayingPhaseRound(
    override val id: RoundId,
    override val gameId: GameId,
    override val roundNumber: NumericValue,
    override val players: List<PlayerId>,
    override val playerScoreBoard: Map<PlayerId, RoundScore>,
    override val initialPlayer: PlayerId,
    override val recordedEvents: List<RoundEvent> = emptyList(),
    override val currentPlayer: PlayerId,
    val triumphsPlayed: NumericValue,
    val currentWinningPlayer: PlayerId,
    val currentWinningCard: Card?,
    val referenceCardGroup: CardGroup?
): PlayerTurnPhaseRound() {
    fun playCard(hand: Hand, card: Card): Round {
        validatePlayingPhase(hand, card)
        return playCardFromHand(hand, card)
    }

    private fun playCardFromHand(hand: Hand, card: Card): Round {
        val newHand = hand.removeCard(card)
        val updatedRound = updateWithMove(newHand, card)
        return updatedRound.movePhaseForward()
    }

    private fun updateWithMove(hand: Hand, card: Card): PlayingPhaseRound {
        val playerId = hand.playerId
        val updatedRound = updateWinningCardAndPlayer(playerId, card)

        return updatedRound.copy(
            recordedEvents = updatedRound.recordedEvents + MoveInRoundRegisteredEvent(updatedRound, hand)
        )
    }

    private fun updateWinningCardAndPlayer(playerId: PlayerId, card: Card): PlayingPhaseRound {
        if (card is FoolCard) {
            return this
        }

        return if (currentWinningCard == null) {
            this.copy(
                currentWinningPlayer = playerId.takeIf { card !is FoolCard} ?: this.currentWinningPlayer,
                currentWinningCard = card.takeIf { it !is FoolCard }
            )
        } else {
            this.copy(
                currentWinningPlayer = playerId
                    .takeIf { card.canBeat(currentWinningCard, referenceCardGroup) }
                    ?: this.currentWinningPlayer,
                currentWinningCard = card.takeIf { it.canBeat(currentWinningCard, referenceCardGroup) }
                    ?: this.currentWinningCard
            )
        }
    }

    private fun movePhaseForward(): Round {
        val nextPlayerId = nextPlayer()

        return if (nextPlayerId == initialPlayer) {
            // summarize and give the triumph to the winning player
            updateForEndOfTriumph(nextPlayerId)
        } else {
            updateForNextPlayer(nextPlayerId)
        }
    }

    private fun updateForEndOfTriumph(nextPlayerId: PlayerId): Round {
        val updatedPlayerScoreBoard = playerScoreBoard.toMutableMap()

        updatedPlayerScoreBoard[currentWinningPlayer] = updatedPlayerScoreBoard[currentWinningPlayer]!!
            .let {
                it.copy(
                    actual = it.actual + NumericValue.ONE
                )
            }

        val updatedRound = this.copy(
            triumphsPlayed = this.triumphsPlayed + NumericValue.ONE,
            playerScoreBoard = updatedPlayerScoreBoard
        )

        return if (updatedRound.triumphsPlayed >= updatedRound.roundNumber) {
            updatedRound.endRound()
        } else {
            updatedRound.updateForNextPlayer(nextPlayerId)
        }
    }

    private fun updateForNextPlayer(nextPlayerId: PlayerId): Round {
        val updatedRound = this.copy(
            currentPlayer = nextPlayerId
        )

        return updatedRound.copy(
            recordedEvents = updatedRound.recordedEvents + PlayingPhaseReadyEvent(updatedRound)
        )
    }

    private fun endRound(): Round {
        return EndedRound.from(this)
    }

    private fun validatePlayingPhase(hand: Hand, card: Card) {
        if (!hand.cards.contains(card)) {
            throw CardNotInHandException(hand, card)
        }

        validatePlayedCardGroup(hand, card)
    }

    private fun validatePlayedCardGroup(hand: Hand, card: Card) {
        if (currentWinningCard == null || currentWinningCard is WizardCard) {
            return
        }

        if (card !is ClassCard) {
            return
        }

        if (card.group == currentWinningCard.group) {
            return
        }
        
        val existingCard = hand.removeCard(card)
            .cards
            .firstOrNull { it.group == currentWinningCard.group }

        if (existingCard != null) {
            throw IllegalMoveException(
                "The card $card cannot be played, the current " +
                "card group that must be played is ${currentWinningCard.group}."
            )
        }
    }

    internal companion object {
        fun from(round: DeclarationPhaseRound): PlayingPhaseRound {
            return with(round) {
                val nextRound = PlayingPhaseRound(
                    id = id,
                    gameId = gameId,
                    roundNumber = roundNumber,
                    players = players,
                    playerScoreBoard = playerScoreBoard,
                    initialPlayer = initialPlayer,
                    recordedEvents = recordedEvents,
                    currentPlayer = initialPlayer,
                    currentWinningPlayer = initialPlayer,
                    referenceCardGroup = referenceCardGroup,
                    currentWinningCard = null,
                    triumphsPlayed = NumericValue.ZERO
                )

                nextRound.copy(recordedEvents = nextRound.recordedEvents + PlayingPhaseReadyEvent(nextRound))
            }
        }
    }
}

data class EndedRound(
    override val id: RoundId,
    override val gameId: GameId,
    override val roundNumber: NumericValue,
    override val players: List<PlayerId>,
    override val playerScoreBoard: Map<PlayerId, RoundScore>,
    override val initialPlayer: PlayerId,
    override val recordedEvents: List<RoundEvent> = emptyList(),
    override val currentPlayer: PlayerId,
    val triumphsPlayed: NumericValue,
    val currentWinningPlayer: PlayerId,
    val currentWinningCard: Card?,
    val referenceCardGroup: CardGroup?
) : PlayerTurnPhaseRound() {
    companion object {
        internal fun from(round: PlayingPhaseRound): Round {
            return with(round) {
                val endedRound = EndedRound(
                    id = id,
                    gameId = gameId,
                    roundNumber = roundNumber,
                    players = players,
                    playerScoreBoard = playerScoreBoard,
                    initialPlayer = initialPlayer,
                    recordedEvents = recordedEvents,
                    currentPlayer = initialPlayer,
                    currentWinningPlayer = initialPlayer,
                    referenceCardGroup = referenceCardGroup,
                    currentWinningCard = currentWinningCard,
                    triumphsPlayed = triumphsPlayed
                )

                endedRound.copy(
                    recordedEvents = recordedEvents + RoundEndedEvent(round)
                )
            }
        }
    }
}
