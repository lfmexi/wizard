package org.lfmexi.wizard.domain.games

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.lfmexi.wizard.domain.Fixtures.LOBBY_GAME
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_1
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_2
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_3
import org.lfmexi.wizard.domain.values.NumericValue

internal class OngoingGameTest {
    @Test
    fun `should create an ongoing game from a LobbyGame`() {
        // given
        val game = LOBBY_GAME.copy()

        // when
        val ongoingGame = OngoingGame.createFrom(game)

        // then
        assertThat(ongoingGame).isInstanceOf(OngoingGame::class.java)

        ongoingGame as OngoingGame

        assertThat(ongoingGame.recordedEvents).hasSize(1)
        assertThat(ongoingGame.recordedEvents.first()).isInstanceOf(GameStartedEvent::class.java)
        assertThat(ongoingGame.ongoingRound).isEqualTo(NumericValue.ZERO)
    }

    @Test
    fun `should start a new round`() {
        // given
        val game = LOBBY_GAME.copy(
            players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3)
        )

        // when
        val ongoingGame = (OngoingGame.createFrom(game) as OngoingGame)
            .copy(recordedEvents = emptyList())

        val newOngoingGame = ongoingGame.nextRound()
        // then
        assertThat(newOngoingGame).isInstanceOf(OngoingGame::class.java)
        newOngoingGame as OngoingGame
        assertThat(newOngoingGame.ongoingRound).isEqualTo(NumericValue(1))

        val event = newOngoingGame.recordedEvents.first() as NextRoundCreatedForGameEvent

        assertThat(event.round.dealingPlayer).isEqualTo(PLAYER_ID_1)
    }

    @Test
    fun `should start a new round with the second player as initial`() {
        // given
        val game = LOBBY_GAME.copy(
            players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3)
        )

        // when
        val ongoingGame = (OngoingGame.createFrom(game) as OngoingGame)
            .copy(
                recordedEvents = emptyList(),
                ongoingRound = NumericValue(1)
            )

        val newOngoingGame = ongoingGame.nextRound()
        // then
        assertThat(newOngoingGame).isInstanceOf(OngoingGame::class.java)
        newOngoingGame as OngoingGame

        assertThat(newOngoingGame.ongoingRound).isEqualTo(NumericValue(2))
        assertThat(newOngoingGame).isInstanceOf(OngoingGame::class.java)

        val event = newOngoingGame.recordedEvents.first() as NextRoundCreatedForGameEvent

        assertThat(event.round.dealingPlayer).isEqualTo(PLAYER_ID_2)
    }

    @Test
    fun `should end the game after the last round`() {
        // given
        val game = LOBBY_GAME.copy(
            players = listOf(PLAYER_ID_1, PLAYER_ID_2, PLAYER_ID_3)
        )

        // when
        val ongoingGame = (OngoingGame.createFrom(game) as OngoingGame)
            .copy(
                recordedEvents = emptyList(),
                ongoingRound = NumericValue(20)
            )

        val newOngoingGame = ongoingGame.nextRound()
        // then
        assertThat(newOngoingGame).isInstanceOf(EndedGame::class.java)
        assertThat(newOngoingGame.recordedEvents.first()).isInstanceOf(GameEndedEvent::class.java)
        newOngoingGame as EndedGame
        assertThat(newOngoingGame.ongoingRound).isEqualTo(NumericValue(20))
    }
}
