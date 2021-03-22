package org.lfmexi.wizard.domain.games

import org.lfmexi.wizard.domain.events.DomainEvent
import org.lfmexi.wizard.domain.exception.NotEnoughPlayersException
import org.lfmexi.wizard.domain.exception.NotGameOwnerStartException
import org.lfmexi.wizard.domain.players.moves.PlayerMove
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.games.rounds.Round
import org.lfmexi.wizard.domain.values.NumericValue

sealed class Game : GameStateChanger {
    abstract val id: GameId
    abstract val players: List<PlayerId>
    abstract val currentRound: Round?
    abstract val endedRounds: List<Round>
    abstract val recordedEvents: List<DomainEvent>

    override fun endGame(): Game {
        return EndedGame.createFrom(this)
    }

    companion object {
        fun createNewGame(owner: PlayerId): Game {
            return LobbyGame.createNewLobbyGame(owner)
        }
    }
}

data class LobbyGame(
    override val id: GameId,
    override val players: List<PlayerId>,
    override val recordedEvents: List<DomainEvent> = emptyList(),
    override val endedRounds: List<Round> = emptyList(),
    val owner: PlayerId
) : Game() {
    override val currentRound: Round? = null

    override fun addPlayer(playerId: PlayerId): Game {
        require(players.size < 6) {
            "Cannot add more players to the game"
        }

        if (players.contains(playerId)) {
            return this
        }

        val lobbyGame = addPlayerToLobby(playerId)

        return if (lobbyGame.players.size == 6) {
            lobbyGame.startGame(owner)
        } else {
            lobbyGame
        }
    }

    override fun startGame(initiator: PlayerId): Game {
        if (initiator != owner) {
            throw NotGameOwnerStartException("$initiator is not the owner of the Game $id")
        }

        if (players.size < 3) {
            throw NotEnoughPlayersException("At least 3 players are require to start the game $id")
        }

        return OngoingGame.createFrom(this)
    }

    override fun endGame(): Game {
        return EndedGame.createFrom(this)
    }

    private fun addPlayerToLobby(playerId: PlayerId): LobbyGame {
        val updatedLobbyGame = this.copy(
            players = this.players + playerId
        )

        return updatedLobbyGame.copy(
            recordedEvents = updatedLobbyGame.recordedEvents + PlayerAddedToGameEvent(updatedLobbyGame)
        )
    }

    companion object {
        fun createNewLobbyGame(owner: PlayerId): Game {
            val lobbyGame = LobbyGame(
                id = GameId.generate(),
                players = listOf(owner),
                owner = owner
            )

            return lobbyGame.copy(
                recordedEvents = listOf(GameCreatedEvent(lobbyGame))
            )
        }
    }
}

data class OngoingGame (
    override val id: GameId,
    override val players: List<PlayerId>,
    override val currentRound: Round? = null,
    override val endedRounds: List<Round> = emptyList(),
    override val recordedEvents: List<DomainEvent> = emptyList(),
    val ongoingRoundNumber: NumericValue = NumericValue.ZERO
) : Game() {
    private val numberOfRounds = NumericValue(60 / players.size)

    init {
        require(ongoingRoundNumber <= numberOfRounds) {
            "Ongoing rounds ($ongoingRoundNumber) for the game $id is bigger " +
                "than the expected number of rounds ($numberOfRounds)"
        }
    }

    fun registerMove(move: PlayerMove): Game {
        require(currentRound != null) {
            "No current active round exists for game $id"
        }

        val updatedRound = currentRound.registerMove(move)
        val updatedGame = this.copy(
            currentRound = updatedRound
        )

        return updatedGame.copy(
            recordedEvents = updatedGame.recordedEvents + MoveRegisteredEvent(updatedGame) + updatedRound.recordedEvents
        )
    }

    override fun nextRound(): Game {
        return this.addCurrentRoundToEndedRounds()
            .updateWithNextRound()
    }

    private fun updateWithNextRound(): Game {
        if (ongoingRoundNumber >= numberOfRounds) {
            return endGame()
        }

        val ongoingGame = this.copy(
            ongoingRoundNumber = ongoingRoundNumber + NumericValue(1)
        )

        val round = Round.createNewRound(ongoingGame, ongoingGame.nextDealingPlayer())

        return ongoingGame.copy(
            recordedEvents = ongoingGame.recordedEvents + NextRoundGeneratedForGameEvent(ongoingGame),
            currentRound = round
        )
    }

    private fun addCurrentRoundToEndedRounds(): OngoingGame {
        return if (currentRound != null) {
            this.copy(
                endedRounds = endedRounds + currentRound
            )
        } else {
            this
        }
    }

    private fun nextDealingPlayer(): PlayerId {
        require(ongoingRoundNumber >= NumericValue.ZERO) {
            "The ongoing round should be already greater than ${NumericValue.ZERO}"
        }

        return players[(ongoingRoundNumber.value - 1) % players.size]
    }

    companion object {
        internal fun createFrom(lobbyGame: LobbyGame): Game {
            return with(lobbyGame) {
                val newOngoingGame = OngoingGame(
                    id = id,
                    players = players,
                    recordedEvents = recordedEvents
                )

                newOngoingGame.copy(
                    recordedEvents = newOngoingGame.recordedEvents + GameStartedEvent(newOngoingGame)
                )
            }
        }
    }
}

data class EndedGame(
    override val id: GameId,
    override val players: List<PlayerId>,
    override val recordedEvents: List<DomainEvent> = emptyList(),
    override val endedRounds: List<Round>,
    val ongoingRound: NumericValue
) : Game() {
    override val currentRound: Round? = null

    companion object {
        fun createFrom(game: Game): EndedGame {
            return with(game) {
                val ongoingRound = when (this) {
                    is LobbyGame -> NumericValue.ZERO
                    is OngoingGame -> ongoingRoundNumber
                    is EndedGame -> ongoingRound
                }

                val endedGame = EndedGame(
                    id = id,
                    players = players,
                    ongoingRound = ongoingRound,
                    recordedEvents = recordedEvents,
                    endedRounds = endedRounds
                )

                endedGame.copy(
                    recordedEvents = endedGame.recordedEvents + GameEndedEvent(endedGame)
                )
            }
        }
    }
}
