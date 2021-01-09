package org.lfmexi.wizard.domain.games

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.lfmexi.wizard.domain.exception.NotEnoughPlayersException
import org.lfmexi.wizard.domain.exception.NotGameOwnerStartException
import org.lfmexi.wizard.domain.players.PlayerId
import org.lfmexi.wizard.domain.values.NumericValue

internal class LobbyGameTest {
    @Test
    fun `should create a new lobby game`() {
        // given
        val playerId = PlayerId.generate()

        // when
        val game = Game.createNewGame(playerId)

        // then
        assertThat(game).isInstanceOf(LobbyGame::class.java)
        assertThat(game.recordedEvents).isNotEmpty
        assertThat(game.recordedEvents.first()).isInstanceOf(GameCreatedEvent::class.java)
    }

    @Test
    fun `should add more players to the game`() {
        // given
        val playerId = PlayerId.generate()
        val player2 = PlayerId.generate()

        val lobbyGame = Game.createNewGame(playerId)

        // when
        val newLobbyGame = lobbyGame.addPlayer(player2)

        // then
        assertThat(newLobbyGame.players).containsExactly(playerId, player2)
        assertThat(newLobbyGame.recordedEvents).isNotEmpty
        assertThat(newLobbyGame.recordedEvents.first()).isInstanceOf(GameCreatedEvent::class.java)
        assertThat(newLobbyGame.recordedEvents[1]).isInstanceOf(PlayerAddedToGameEvent::class.java)
    }

    @Test
    fun `adding players should be idempotent`() {
        // given
        val playerId = PlayerId.generate()
        val player2 = PlayerId.generate()

        val lobbyGame = Game.createNewGame(playerId)

        // when
        val newLobbyGame = lobbyGame.addPlayer(player2)
            .addPlayer(player2)

        // then
        assertThat(newLobbyGame.players).containsExactly(playerId, player2)
        assertThat(newLobbyGame.recordedEvents).isNotEmpty
        assertThat(newLobbyGame.recordedEvents.first()).isInstanceOf(GameCreatedEvent::class.java)
        assertThat(newLobbyGame.recordedEvents[1]).isInstanceOf(PlayerAddedToGameEvent::class.java)
    }

    @Test
    fun `should start the game when 6 are in the lobby`() {
        // given
        val player1 = PlayerId.generate()
        val player2 = PlayerId.generate()
        val player3 = PlayerId.generate()
        val player4 = PlayerId.generate()
        val player5 = PlayerId.generate()
        val player6 = PlayerId.generate()

        val lobbyGame = (Game.createNewGame(player1) as LobbyGame)
            .copy(
                players = listOf(
                    player1, player2, player3, player4, player5
                ),
                recordedEvents = emptyList()
            )

        // when
        val ongoingGame = lobbyGame.addPlayer(player6)

        // then
        assertThat(ongoingGame).isInstanceOf(OngoingGame::class.java)

        ongoingGame as OngoingGame

        assertThat(ongoingGame.recordedEvents).hasSize(2)
        assertThat(ongoingGame.recordedEvents.first()).isInstanceOf(PlayerAddedToGameEvent::class.java)
        assertThat(ongoingGame.recordedEvents[1]).isInstanceOf(GameStartedEvent::class.java)
        assertThat(ongoingGame.ongoingRoundNumber).isEqualTo(NumericValue.ZERO)
    }

    @Test
    fun `should start with 3 players at least`() {
        // given
        val player1 = PlayerId.generate()
        val player2 = PlayerId.generate()
        val player3 = PlayerId.generate()

        val lobbyGame = (Game.createNewGame(player1) as LobbyGame)
            .copy(
                players = listOf(
                    player1, player2, player3
                ),
                recordedEvents = emptyList()
            )

        // when
        val ongoingGame = lobbyGame.startGame(player1)

        // then
        assertThat(ongoingGame).isInstanceOf(OngoingGame::class.java)

        ongoingGame as OngoingGame

        assertThat(ongoingGame.recordedEvents).hasSize(1)
        assertThat(ongoingGame.recordedEvents.first()).isInstanceOf(GameStartedEvent::class.java)
        assertThat(ongoingGame.ongoingRoundNumber).isEqualTo(NumericValue.ZERO)
    }

    @Test
    fun `should end the game without starting it`() {
        // given
        val playerId = PlayerId.generate()
        val game = (Game.createNewGame(playerId) as LobbyGame)
            .copy(recordedEvents = emptyList())

        // when
        val endedGame = game.endGame()

        // then
        assertThat(endedGame).isInstanceOf(EndedGame::class.java)
        assertThat(endedGame.recordedEvents).isNotEmpty
        assertThat(endedGame.recordedEvents.first()).isInstanceOf(GameEndedEvent::class.java)
        // Not even started

        endedGame as EndedGame
        assertThat(endedGame.ongoingRound).isEqualTo(NumericValue.ZERO)
    }

    @Test
    fun `should not start if the initiator is not the owner of the game`() {
        // given
        val player1 = PlayerId.generate()
        val player2 = PlayerId.generate()
        val player3 = PlayerId.generate()

        val lobbyGame = (Game.createNewGame(player1) as LobbyGame)
            .copy(
                players = listOf(
                    player1, player2, player3
                ),
                recordedEvents = emptyList()
            )

        // when - then
        assertThrows<NotGameOwnerStartException> {
            lobbyGame.startGame(player2)
        }
    }

    @Test
    fun `should not start the game if there are not enough players`() {
        // given
        val player1 = PlayerId.generate()
        val player2 = PlayerId.generate()

        val lobbyGame = (Game.createNewGame(player1) as LobbyGame)
            .copy(
                players = listOf(
                    player1, player2
                ),
                recordedEvents = emptyList()
            )

        // when - then
        assertThrows<NotEnoughPlayersException> {
            lobbyGame.startGame(player1)
        }
    }
}
