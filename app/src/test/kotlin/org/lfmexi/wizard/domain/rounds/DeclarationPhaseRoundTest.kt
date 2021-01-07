package org.lfmexi.wizard.domain.rounds

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.lfmexi.wizard.domain.Fixtures.DECLARATION_PHASE_ROUND
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_1
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_2
import org.lfmexi.wizard.domain.Fixtures.PLAYER_ID_3
import org.lfmexi.wizard.domain.exception.ExpectedScoreNotAcceptedException
import org.lfmexi.wizard.domain.exception.NotInTurnException
import org.lfmexi.wizard.domain.scoring.RoundScore
import org.lfmexi.wizard.domain.values.NumericValue

internal class DeclarationPhaseRoundTest {
    @Test
    fun `should not create a declaration when the player is not in turn`() {
        val round = DECLARATION_PHASE_ROUND.copy(
            currentPlayer = PLAYER_ID_3
        )

        val playerId = PLAYER_ID_1

        assertThrows<NotInTurnException> {
            round.declare(playerId, NumericValue(1))
        }
    }

    @Test
    fun `should not create a declaration when the declaration matches with the number of triumphs`() {
        val playerId = PLAYER_ID_1

        val round = DECLARATION_PHASE_ROUND.copy(
            roundNumber = NumericValue(2),
            dealingPlayer = PLAYER_ID_1,
            currentPlayer = playerId,
            playerScoreBoard = mapOf(
                PLAYER_ID_1 to RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
            )
        )

        assertThrows<ExpectedScoreNotAcceptedException> {
            round.declare(playerId, NumericValue.ZERO)
        }
    }

    @Test
    fun `should continue with the next player after a successful declaration`() {
        // given
        val playerId = PLAYER_ID_2

        val round = DECLARATION_PHASE_ROUND.copy(
            roundNumber = NumericValue(2),
            dealingPlayer = PLAYER_ID_1,
            currentPlayer = playerId,
            playerScoreBoard = mapOf(
                PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_2 to RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                PLAYER_ID_3 to RoundScore(NumericValue.ZERO, NumericValue.ZERO)
            )
        )

        // when
        val newRound = round.declare(playerId, NumericValue.ONE)

        // then
        assertThat(newRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(
                DECLARATION_PHASE_ROUND.copy(
                    roundNumber = NumericValue(2),
                    currentPlayer = PLAYER_ID_3,
                    playerScoreBoard = mapOf(
                        PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                        PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                        PLAYER_ID_3 to RoundScore(NumericValue.ZERO, NumericValue.ZERO)
                    )
                )
            )

        assertThat(newRound.recordedEvents).isNotEmpty
        assertThat(newRound.recordedEvents.first()).isInstanceOf(DeclarationDoneEvent::class.java)
    }

    @Test
    fun `should continue with the next player, even if the initial player is not the first one on the players list`() {
        // given
        val playerId = PLAYER_ID_1

        val round = DECLARATION_PHASE_ROUND.copy(
            roundNumber = NumericValue(4),
            dealingPlayer = PLAYER_ID_2,
            initialPlayer = PLAYER_ID_3,
            currentPlayer = playerId,
            playerScoreBoard = mapOf(
                PLAYER_ID_1 to RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                PLAYER_ID_2 to RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                PLAYER_ID_3 to RoundScore(NumericValue(2), NumericValue.ZERO)
            )
        )

        // when
        val newRound = round.declare(playerId, NumericValue.ONE)

        // then
        assertThat(newRound)
            .usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(
                DECLARATION_PHASE_ROUND.copy(
                    roundNumber = NumericValue(4),
                    currentPlayer = PLAYER_ID_2,
                    dealingPlayer = PLAYER_ID_2,
                    initialPlayer = PLAYER_ID_3,
                    playerScoreBoard = mapOf(
                        PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                        PLAYER_ID_2 to RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                        PLAYER_ID_3 to RoundScore(NumericValue(2), NumericValue.ZERO)
                    )
                )
            )

        assertThat(newRound.recordedEvents).isNotEmpty
        assertThat(newRound.recordedEvents.first()).isInstanceOf(DeclarationDoneEvent::class.java)
    }

    @Test
    fun `should continue with the next phase when the last player had its declaration`() {
        // given
        val playerId = PLAYER_ID_1

        val round = DECLARATION_PHASE_ROUND.copy(
            roundNumber = NumericValue(2),
            dealingPlayer = PLAYER_ID_1,
            initialPlayer = PLAYER_ID_2,
            currentPlayer = playerId,
            playerScoreBoard = mapOf(
                PLAYER_ID_1 to RoundScore(NumericValue.ZERO, NumericValue.ZERO),
                PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
            )
        )

        // when
        val newRound = round.declare(playerId, NumericValue.ONE)

        // then
        assertThat(newRound).usingRecursiveComparison()
            .ignoringFields("recordedEvents")
            .isEqualTo(
                PlayingPhaseRound(
                    id = round.id,
                    currentPlayer = PLAYER_ID_2,
                    gameId = round.gameId,
                    dealingPlayer = round.dealingPlayer,
                    initialPlayer = round.initialPlayer,
                    players = round.players,
                    roundNumber = round.roundNumber,
                    playerScoreBoard = mapOf(
                        PLAYER_ID_1 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                        PLAYER_ID_2 to RoundScore(NumericValue.ONE, NumericValue.ZERO),
                        PLAYER_ID_3 to RoundScore(NumericValue.ONE, NumericValue.ZERO)
                    ),
                    triumphCardGroup = round.referenceCardGroup,
                    currentWinningCard = null,
                    playingCardGroup = null,
                    currentWinningPlayer = round.initialPlayer,
                    triumphsPlayed = NumericValue.ZERO
                )
            )

        assertThat(newRound.recordedEvents).isNotEmpty
        assertThat(newRound.recordedEvents.first()).isInstanceOf(PlayingPhaseReadyEvent::class.java)
    }
}
