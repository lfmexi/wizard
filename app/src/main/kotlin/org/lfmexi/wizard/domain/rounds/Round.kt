package org.lfmexi.wizard.domain.rounds

import org.lfmexi.wizard.domain.cards.Card
import org.lfmexi.wizard.domain.cards.CardGroup
import org.lfmexi.wizard.domain.cards.ClassCard
import org.lfmexi.wizard.domain.cards.FoolCard
import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.exception.CardNotInHandException
import org.lfmexi.wizard.domain.exception.ExpectedScoreNotAcceptedException
import org.lfmexi.wizard.domain.exception.IllegalMoveException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.games.GameId
import org.lfmexi.wizard.domain.games.OngoingGame
import org.lfmexi.wizard.domain.hands.Hand
import org.lfmexi.wizard.domain.moves.DealCardsMove
import org.lfmexi.wizard.domain.moves.DeclarationMove
import org.lfmexi.wizard.domain.moves.Move
import org.lfmexi.wizard.domain.moves.PlayCardMove
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.scoring.RoundScore
import org.lfmexi.wizard.domain.values.NumericValue

sealed class Round {
    abstract val id: RoundId
    abstract val gameId: GameId
    abstract val roundNumber: NumericValue
    abstract val players: List<PlayerId>
    abstract val playerScoreBoard: Map<PlayerId, RoundScore>
    abstract val dealingPlayer: PlayerId
    abstract val moves: List<Move>
    abstract val recordedEvents: List<DomainEvent>

    abstract fun registerMove(move: Move): Round

    companion object {
        fun createNewRound(
            game: OngoingGame,
            nextDealingPlayerId: PlayerId
        ): Round {
            return DealingPhaseRound.createNewRound(
                game = game,
                dealingPlayer = nextDealingPlayerId
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
    override val dealingPlayer: PlayerId,
    override val moves: List<Move> = emptyList(),
    override val recordedEvents: List<RoundEvent> = emptyList()
) : Round() {

    override fun registerMove(move: Move): Round {
        return when (move) {
            is DealCardsMove -> {
                validate(move)
                val (hands, activeCard) = move.deal(this)
                this.copy(
                    moves = moves + move
                ).registerDeal(hands, activeCard)
            }
            else -> throw IllegalMoveException("Move is not expected")
        }
    }

    private fun validate(move: Move) {
        with(move) {
            if (playerId != dealingPlayer) {
                throw NotInTurnException(playerId)
            }
        }
    }

    /**
     * Once a [Round] is created, a deal phase starts.
     * @return a [Pair] of [Round] and [List] of [Hand]
     */
    private fun registerDeal(hands: List<Hand>, activeCard: Card?): Round {
        return DeclarationPhaseRound.from(
            round = this,
            initialPlayer = nextToDealer(),
            hands = hands,
            referenceCardGroup = activeCard?.group
        )
    }

    private fun nextToDealer(): PlayerId {
        val nextPlayerIndex = players.indexOf(dealingPlayer) + 1

        return if (nextPlayerIndex >= players.size) {
            players.first()
        } else {
            players[nextPlayerIndex]
        }
    }

    internal companion object {
        fun createNewRound(
            game: OngoingGame,
            dealingPlayer: PlayerId
        ): Round {
            val round = DealingPhaseRound(
                id = RoundId.generate(),
                gameId = game.id,
                roundNumber = game.ongoingRoundNumber,
                players = game.players,
                playerScoreBoard = game.players.map { it to RoundScore.ZERO_SCORE }.toMap(),
                dealingPlayer = dealingPlayer
            )

            return round.copy(
                recordedEvents = listOf(RoundCreatedEvent(round))
            )
        }
    }
}

sealed class PlayerTurnPhaseRound : Round() {
    abstract val initialPlayer: PlayerId
    abstract val currentPlayer: PlayerId
    abstract val hands: List<Hand>

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
    override val dealingPlayer: PlayerId,
    override val recordedEvents: List<DomainEvent> = emptyList(),
    override val initialPlayer: PlayerId,
    override val currentPlayer: PlayerId,
    override val moves: List<Move> = emptyList(),
    override val hands: List<Hand>,
    val referenceCardGroup: CardGroup?
): PlayerTurnPhaseRound() {
    override fun registerMove(move: Move): Round {
        return when(move) {
            is DeclarationMove -> {
                validateDeclaration(move)
                this.copy(
                    moves = moves + move
                ).declare(move.playerId, move.triumphsDeclared)
            }
            else -> throw IllegalMoveException("Should be a declaration move")
        }
    }

    /**
     * Every player must declare how many times they will win in this round. This will trigger
     * a new turn creation
     */
    fun declare(playerId: PlayerId, value: NumericValue): Round {
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
            declaredRound
        }
    }

    private fun validateDeclaration(move: DeclarationMove) {
        with(move) {
            if (playerId != currentPlayer) {
                throw NotInTurnException(playerId)
            }

            if (isLastPlayer()) {
                val sumOfExpected = playerScoreBoard.map { (_, score) -> score.expected }
                    .sumOf { it.value }
                    .let { NumericValue(it) } + triumphsDeclared

                if (sumOfExpected == roundNumber) throw ExpectedScoreNotAcceptedException(triumphsDeclared, roundNumber)
            }
        }
    }

    private fun isLastPlayer(): Boolean {
        return nextPlayer() == initialPlayer
    }

    internal companion object {
        fun from(
            round: Round,
            initialPlayer: PlayerId,
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
                    dealingPlayer = dealingPlayer,
                    moves = moves,
                    hands = hands,
                    recordedEvents = recordedEvents,
                    initialPlayer = initialPlayer,
                    currentPlayer = initialPlayer,
                    referenceCardGroup = referenceCardGroup
                )

                nextRound.copy(
                    recordedEvents = nextRound.recordedEvents + DeclarationPhaseReadyEvent(nextRound)
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
    override val dealingPlayer: PlayerId,
    override val recordedEvents: List<DomainEvent> = emptyList(),
    override val initialPlayer: PlayerId,
    override val currentPlayer: PlayerId,
    override val moves: List<Move> = emptyList(),
    override val hands: List<Hand>,
    val triumphsPlayed: NumericValue,
    val currentWinningPlayer: PlayerId,
    val currentWinningCard: Card?,
    val triumphCardGroup: CardGroup?,
    val playingCardGroup: CardGroup?
): PlayerTurnPhaseRound() {
    private val hasPlayingCardGroup = playingCardGroup != null

    override fun registerMove(move: Move): Round {
        return when(move) {
            is PlayCardMove -> {
                validate(move)
                this.copy(
                    moves = moves + move
                ).registerPlayedCard(move.playerId, move.card)
            }
            else -> throw IllegalMoveException("Not a play card move")
        }
    }

    private fun registerPlayedCard(playerId: PlayerId, card: Card): Round {
        return updateWithCard(playerId, card)
            .movePhaseForward()
    }

    private fun updateWithCard(playerId: PlayerId, card: Card): PlayingPhaseRound {
        return updateHand(playerId, card)
            .updatePlayingCardGroup(card)
            .updateWinningCardAndPlayer(playerId, card)
    }

    private fun updateHand(playerId: PlayerId, card: Card): PlayingPhaseRound {
        val hand = hands.first { it.playerId == playerId }

        val newHand = hand.playCard(card)

        return this.copy(
            hands = hands - hand + newHand
        )
    }

    private fun updatePlayingCardGroup(card: Card): PlayingPhaseRound {
        if (card is FoolCard) {
            return this
        }

        if (playingCardGroup != null) {
            return this
        }

        return this.copy(
            playingCardGroup = card.group
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
                    .takeIf { card.canBeat(currentWinningCard, triumphCardGroup) }
                    ?: this.currentWinningPlayer,
                currentWinningCard = card.takeIf { it.canBeat(currentWinningCard, triumphCardGroup) }
                    ?: this.currentWinningCard
            )
        }
    }

    private fun movePhaseForward(): Round {
        val nextPlayerId = nextPlayer()

        return if (nextPlayerId == initialPlayer) {
            // summarize and give the triumph to the winning player
            updateForEndOfTriumph()
        } else {
            updateForNextPlayer(nextPlayerId)
        }
    }

    private fun updateForEndOfTriumph(): Round {
        val updatedPlayerScoreBoard = playerScoreBoard.toMutableMap()

        updatedPlayerScoreBoard[currentWinningPlayer] = updatedPlayerScoreBoard[currentWinningPlayer]!!
            .let {
                it.copy(
                    actual = it.actual + NumericValue.ONE
                )
            }

        val updatedRound = this.copy(
            triumphsPlayed = this.triumphsPlayed + NumericValue.ONE,
            playerScoreBoard = updatedPlayerScoreBoard,
            currentWinningCard = null,
            playingCardGroup = null
        )

        val updatedRoundWithEvent = updatedRound.copy(
            recordedEvents = updatedRound.recordedEvents + TriumphEndedEvent(updatedRound)
        )

        return if (updatedRoundWithEvent.triumphsPlayed >= updatedRoundWithEvent.roundNumber) {
            updatedRoundWithEvent.endRound()
        } else {
            updatedRoundWithEvent.updateForNextPlayer(currentWinningPlayer)
        }
    }

    private fun updateForNextPlayer(nextPlayerId: PlayerId): Round {
        return this.copy(
            currentPlayer = nextPlayerId
        )
    }

    private fun endRound(): Round {
        return EndedRound.from(this)
    }

    private fun validate(move: PlayCardMove) {
        validatePlayer(move)
        validateCardFromHand(move)
        validatePlayedCardGroup(move)
    }

    private fun validatePlayer(move: PlayCardMove) {
        if (move.playerId != currentPlayer) {
            throw NotInTurnException(move.playerId)
        }
    }

    private fun validateCardFromHand(move: PlayCardMove) {
        val hand = hands.first { it.playerId == move.playerId }
        if (!hand.cards.contains(move.card)) {
            throw CardNotInHandException(hand, move.card)
        }
    }

    private fun validatePlayedCardGroup(move: PlayCardMove) {
        if (move.card !is ClassCard) {
            return
        }

        if (!hasPlayingCardGroup) {
            return
        }

        if (move.card.group == playingCardGroup) {
            return
        }

        val hand = hands.first { it.playerId == move.playerId }

        val existingCard = hand.playCard(move.card)
            .cards
            .firstOrNull { it.group == playingCardGroup }

        if (existingCard != null) {
            throw IllegalMoveException(
                "The card ${move.card} cannot be played, the current " +
                    "card group that must be played is ${playingCardGroup}."
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
                    dealingPlayer = dealingPlayer,
                    recordedEvents = recordedEvents,
                    currentPlayer = initialPlayer,
                    initialPlayer = initialPlayer,
                    currentWinningPlayer = initialPlayer,
                    triumphCardGroup = referenceCardGroup,
                    hands = hands,
                    currentWinningCard = null,
                    playingCardGroup = null,
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
    override val dealingPlayer: PlayerId,
    override val recordedEvents: List<DomainEvent> = emptyList(),
    override val initialPlayer: PlayerId,
    override val currentPlayer: PlayerId,
    override val moves: List<Move> = emptyList(),
    override val hands: List<Hand>,
    val triumphsPlayed: NumericValue,
    val currentWinningPlayer: PlayerId,
    val currentWinningCard: Card?,
    val referenceCardGroup: CardGroup?
) : PlayerTurnPhaseRound() {
    override fun registerMove(move: Move): Round {
        throw IllegalMoveException("Round already ended. Wait for next round to be created")
    }

    companion object {
        internal fun from(round: PlayingPhaseRound): Round {
            return with(round) {
                val endedRound = EndedRound(
                    id = id,
                    gameId = gameId,
                    roundNumber = roundNumber,
                    players = players,
                    playerScoreBoard = playerScoreBoard,
                    dealingPlayer = dealingPlayer,
                    recordedEvents = recordedEvents,
                    initialPlayer = initialPlayer,
                    currentPlayer = initialPlayer,
                    currentWinningPlayer = currentWinningPlayer,
                    referenceCardGroup = triumphCardGroup,
                    currentWinningCard = currentWinningCard,
                    hands = hands,
                    moves = moves,
                    triumphsPlayed = triumphsPlayed
                )

                endedRound.copy(
                    recordedEvents = recordedEvents + RoundEndedEvent(round)
                )
            }
        }
    }
}
