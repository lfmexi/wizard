package org.lfmexi.wizard.games.domain.rounds

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.lfmexi.wizard.common.Fixtures.ONGOING_GAME

internal class RoundTest {
    @Test
    fun `should create a new round`() {
        val game = ONGOING_GAME.copy()
        val initialPlayer = game.players.first()

        val round = Round.createNewRound(
            game = game,
            nextDealingPlayerId = initialPlayer
        )

        assertThat(round).isInstanceOf(DealingPhaseRound::class.java)
        assertThat(round)
            .usingRecursiveComparison()
            .ignoringFields("id", "recordedEvents")
            .isEqualTo(
                DealingPhaseRound(
                    id = RoundId.generate(),
                    gameId = game.id,
                    roundNumber = game.ongoingRoundNumber,
                    dealingPlayer = initialPlayer,
                    players = game.players,
                    playerScoreBoard = game.players.map { it to RoundScore.ZERO_SCORE }.toMap(),
                )
            )

        assertThat(round.recordedEvents).hasSize(1)
        assertThat(round.recordedEvents.first()).isInstanceOf(RoundCreatedEvent::class.java)
    }
}
