package org.lfmexi.wizard.domain.games

import org.lfmexi.wizard.domain.cards.Deck
import org.lfmexi.wizard.domain.exception.NotEnoughPlayersException
import org.lfmexi.wizard.domain.exception.NotGameOwnerStartException
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.rounds.Round
import org.lfmexi.wizard.domain.values.NumericValue

sealed class Game : GameStateChanger {
    abstract val id: GameId
    abstract val players: List<PlayerId>
    abstract val recordedEvents: List<GameEvent>

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
    override val recordedEvents: List<GameEvent> = emptyList(),
    val owner: PlayerId
) : Game() {
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
            recordedEvents = updatedLobbyGame.recordedEvents + PlayerAddedEvent(updatedLobbyGame)
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
    override val recordedEvents: List<GameEvent>,
    val ongoingRound: NumericValue = NumericValue.ZERO
) : Game() {
    private val deck = Deck.initialize()
    private val numberOfRounds = NumericValue(deck.size.value / players.size)

    init {
        require(ongoingRound <= numberOfRounds) {
            "Ongoing rounds ($ongoingRound) for the game $id is bigger " +
                "than the expected number of rounds ($numberOfRounds)"
        }
    }

    override fun nextRound(): Game {
        if (ongoingRound >= numberOfRounds) {
            return endGame()
        }

        val ongoingGame = this.copy(
            ongoingRound = ongoingRound + NumericValue(1)
        )

        val round = Round.createNewRound(
            game = ongoingGame,
            players = ongoingGame.players,
            initialPlayer = ongoingGame.nextStartingPlayer(),
            deck = deck
        )

        return ongoingGame.copy(
            recordedEvents = ongoingGame.recordedEvents + NextRoundCreatedForGameEvent(ongoingGame, round)
        )
    }

    private fun nextStartingPlayer(): PlayerId {
        return players[(ongoingRound.value - 1) % players.size]
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
    override val recordedEvents: List<GameEvent>,
    val ongoingRound: NumericValue
) : Game() {
    companion object {
        fun createFrom(game: Game): EndedGame {
            return with(game) {
                val ongoingRound = when (this) {
                    is LobbyGame -> NumericValue.ZERO
                    is OngoingGame -> ongoingRound
                    is EndedGame -> ongoingRound
                }

                val endedGame = EndedGame(
                    id = id,
                    players = players,
                    ongoingRound = ongoingRound,
                    recordedEvents = recordedEvents
                )

                endedGame.copy(
                    recordedEvents = endedGame.recordedEvents + GameEndedEvent(endedGame)
                )
            }
        }
    }
}